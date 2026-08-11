import { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import { Card, CardContent, CardHeader, CardTitle } from '#components/ui/card'
import { Button } from '#components/ui/button'
import { Input } from '#components/ui/input'
import { Label } from '#components/ui/label'
import { Pencil, Save, X, Trash2, UserPlus, LogOut } from 'lucide-react'
import { useTranslation } from 'react-i18next'
import { useAuth } from '../../auth/AuthContext'
import { getTeamByName, getMyTeams, updateTeam, deleteTeam } from '@/services/team/teamApi'
import { createPlayer, updatePlayer, softDeletePlayer } from '@/services/player/playerApi'

const positionOptions = ['SETTER', 'WING_SPIKER', 'MIDDLE_BLOCKER', 'OPPOSITE', 'LIBERO']

function PlayerCard({ player, isOwner, onEdit, onDelete, t }) {
    return (
        <Card size="sm">
            <CardHeader>
                <CardTitle className="flex items-center gap-2">
                    <span className="flex h-8 w-8 items-center justify-center rounded-full bg-primary text-xs font-bold text-primary-foreground">
                        {player.dorsal}
                    </span>
                    {player.name} {player.surname}
                </CardTitle>
            </CardHeader>
            <CardContent className="space-y-1 text-sm">
                <p><span className="text-muted-foreground">{t('page.positions.SETTER') === 'Colocador' ? 'Posición:' : 'Position:'}</span> {t(`page.positions.${player.corePosition}`) || player.corePosition}</p>
                <p className="truncate"><span className="text-muted-foreground">{t('page.email')}:</span> {player.email}</p>
                {isOwner && (
                    <div className="flex gap-2 pt-2">
                        <Button variant="outline" size="sm" onClick={() => onEdit(player)}>
                            <Pencil className="size-3" /> {t('page.editPlayer')}
                        </Button>
                        <Button variant="ghost" size="sm" className="text-red-600" onClick={() => onDelete(player)}>
                            <Trash2 className="size-3" /> {t('page.removePlayer')}
                        </Button>
                    </div>
                )}
            </CardContent>
        </Card>
    )
}

const emptyPlayerForm = { name: '', surname: '', email: '', dorsal: '', corePosition: 'SETTER' }

export default function TeamPage() {
    const { t } = useTranslation('teams')
    const { isManager } = useAuth()
    const { name } = useParams()
    const [team, setTeam] = useState(null)
    const [myTeamIds, setMyTeamIds] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState(null)

    // team editing
    const [editing, setEditing] = useState(false)
    const [teamForm, setTeamForm] = useState({ name: '', foundationDate: '', logoUrl: '' })
    const [savingTeam, setSavingTeam] = useState(false)

    // player management
    const [showAddPlayer, setShowAddPlayer] = useState(false)
    const [playerForm, setPlayerForm] = useState(emptyPlayerForm)
    const [editingPlayerId, setEditingPlayerId] = useState(null)
    const [savingPlayer, setSavingPlayer] = useState(false)

    useEffect(() => {
        if (!name) return
        let cancelled = false
        getTeamByName(name)
            .then(team => { if (!cancelled) setTeam(team) })
            .catch(err => { if (!cancelled) setError(err.message) })
            .finally(() => { if (!cancelled) setLoading(false) })
        return () => { cancelled = true }
    }, [name])

    useEffect(() => {
        if (!isManager) return
        let cancelled = false
        getMyTeams()
            .then(ts => { if (!cancelled) setMyTeamIds(ts.map(t => t.id)) })
            .catch(() => { if (!cancelled) setMyTeamIds([]) })
        return () => { cancelled = true }
    }, [isManager])

    const isOwner = isManager && team?.id != null && myTeamIds.includes(team.id)

    if (loading) {
        return <div className="flex items-center justify-center min-h-screen">{t('page.loading')}</div>
    }

    if (error || !team) {
        return (
            <div className="mx-auto max-w-2xl space-y-4 rounded-lg border bg-background p-6">
                <div className="space-y-2">
                    <h1 className="text-2xl font-bold">{t('page.notFound')}</h1>
                    <p className="text-sm text-muted-foreground">
                        {t('page.notFoundDescription')}
                    </p>
                </div>
                <div className="flex gap-2">
                    <Button variant="outline" asChild>
                        <Link to="/teams">{t('page.allTeams')}</Link>
                    </Button>
                    <Button variant="outline" asChild>
                        <Link to="/">{t('page.home')}</Link>
                    </Button>
                </div>
            </div>
        )
    }

    const startEditTeam = () => {
        setTeamForm({
            name: team.name,
            foundationDate: team.foundationDate || '',
            logoUrl: team.logoUrl || '',
        })
        setEditing(true)
    }

    const saveTeam = async () => {
        setSavingTeam(true)
        setError(null)
        try {
            const updated = await updateTeam(team.id, {
                name: teamForm.name,
                foundationDate: teamForm.foundationDate,
                logoUrl: teamForm.logoUrl || null,
            })
            setTeam(updated)
            setEditing(false)
        } catch (err) {
            setError(err.message)
        } finally {
            setSavingTeam(false)
        }
    }

    const removeTeam = async () => {
        if (!window.confirm(t('page.deleteConfirm', { name: team.name }))) return
        setError(null)
        try {
            await deleteTeam(team.id)
            window.location.href = '/teams'
        } catch (err) {
            setError(err.message)
        }
    }

    const submitPlayer = async (e) => {
        e.preventDefault()
        setSavingPlayer(true)
        setError(null)
        const payload = {
            name: playerForm.name,
            surname: playerForm.surname,
            email: playerForm.email,
            dorsal: Number(playerForm.dorsal),
            corePosition: playerForm.corePosition,
        }
        try {
            if (editingPlayerId) {
                const updated = await updatePlayer(editingPlayerId, payload)
                setTeam(prev => ({ ...prev, players: prev.players.map(p => p.id === updated.id ? updated : p) }))
            } else {
                const created = await createPlayer(team.id, payload)
                setTeam(prev => ({ ...prev, players: [...prev.players, created] }))
            }
            setPlayerForm(emptyPlayerForm)
            setShowAddPlayer(false)
            setEditingPlayerId(null)
        } catch (err) {
            setError(err.message)
        } finally {
            setSavingPlayer(false)
        }
    }

    const startEditPlayer = (player) => {
        setEditingPlayerId(player.id)
        setPlayerForm({
            name: player.name,
            surname: player.surname,
            email: player.email,
            dorsal: String(player.dorsal),
            corePosition: player.corePosition,
        })
        setShowAddPlayer(true)
    }

    const removePlayer = async (player) => {
        if (!window.confirm(t('page.removePlayerConfirm', { name: player.name, surname: player.surname }))) return
        setError(null)
        try {
            await softDeletePlayer(player.id)
            setTeam(prev => ({ ...prev, players: prev.players.filter(p => p.id !== player.id) }))
        } catch (err) {
            setError(err.message)
        }
    }

    return (
        <div className="space-y-6">
            <div className="flex items-center justify-between">
                <div className="space-y-1">
                    <div className="flex items-center gap-3">
                        <h1 className="text-2xl font-bold">{team.name}</h1>
                        {isOwner && (
                            <span className="rounded-full bg-primary/10 px-2 py-0.5 text-xs font-medium text-primary">
                                {t('page.myTeam')}
                            </span>
                        )}
                    </div>
                    {team.foundationDate && (
                        <p className="text-sm text-muted-foreground">{t('page.foundedOn', { date: team.foundationDate })}</p>
                    )}
                </div>
                <div className="flex gap-2">
                    {isOwner && !editing && (
                        <>
                            <Button variant="outline" size="sm" onClick={startEditTeam}>
                                <Pencil className="size-3" /> {t('page.editTeam')}
                            </Button>
                            <Button variant="ghost" size="sm" className="text-red-600" onClick={removeTeam}>
                                <LogOut className="size-3" /> {t('page.deleteTeam')}
                            </Button>
                        </>
                    )}
                    <Button variant="outline" asChild>
                        <Link to="/teams">{t('page.allTeams')}</Link>
                    </Button>
                </div>
            </div>

            {error && (
                <div className="rounded-lg border border-red-200 bg-red-50 p-3 text-sm text-red-700">{error}</div>
            )}

            {isOwner && editing && (
                <Card>
                    <CardHeader>
                        <CardTitle>{t('page.editTitle')}</CardTitle>
                    </CardHeader>
                    <CardContent className="space-y-4">
                        <div className="grid gap-2">
                            <Label htmlFor="teamName">{t('page.teamName')}</Label>
                            <Input id="teamName" value={teamForm.name} onChange={e => setTeamForm({ ...teamForm, name: e.target.value })} />
                        </div>
                        <div className="grid gap-2">
                            <Label htmlFor="foundationDate">{t('page.foundationDate')}</Label>
                            <Input id="foundationDate" type="date" value={teamForm.foundationDate} onChange={e => setTeamForm({ ...teamForm, foundationDate: e.target.value })} />
                        </div>
                        <div className="grid gap-2">
                            <Label htmlFor="logoUrl">{t('page.logoUrl')}</Label>
                            <Input id="logoUrl" type="url" placeholder={t('page.logoPlaceholder')} value={teamForm.logoUrl} onChange={e => setTeamForm({ ...teamForm, logoUrl: e.target.value })} />
                        </div>
                        <div className="flex gap-2">
                            <Button size="sm" onClick={saveTeam} disabled={savingTeam}>
                                <Save className="size-3" /> {savingTeam ? t('page.saving') : t('page.save')}
                            </Button>
                            <Button variant="outline" size="sm" onClick={() => setEditing(false)}>
                                <X className="size-3" /> {t('page.cancel')}
                            </Button>
                        </div>
                    </CardContent>
                </Card>
            )}

            {isOwner && (
                <div className="flex items-center justify-between">
                    <h2 className="text-lg font-semibold">{t('page.roster', { count: team.players?.length ?? 0 })}</h2>
                    <Button size="sm" onClick={() => { setShowAddPlayer(!showAddPlayer); setEditingPlayerId(null); setPlayerForm(emptyPlayerForm) }}>
                        <UserPlus className="size-3" /> {t('page.addPlayer')}
                    </Button>
                </div>
            )}

            {isOwner && showAddPlayer && (
                <Card>
                    <CardHeader>
                        <CardTitle>{editingPlayerId ? t('page.editPlayer') : t('page.addPlayerTitle')}</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <form onSubmit={submitPlayer} className="grid gap-4 sm:grid-cols-2">
                            <div className="grid gap-2">
                                <Label htmlFor="pName">{t('page.name')}</Label>
                                <Input id="pName" required value={playerForm.name} onChange={e => setPlayerForm({ ...playerForm, name: e.target.value })} />
                            </div>
                            <div className="grid gap-2">
                                <Label htmlFor="pSurname">{t('page.surname')}</Label>
                                <Input id="pSurname" required value={playerForm.surname} onChange={e => setPlayerForm({ ...playerForm, surname: e.target.value })} />
                            </div>
                            <div className="grid gap-2">
                                <Label htmlFor="pEmail">{t('page.email')}</Label>
                                <Input id="pEmail" type="email" required value={playerForm.email} onChange={e => setPlayerForm({ ...playerForm, email: e.target.value })} />
                            </div>
                            <div className="grid gap-2">
                                <Label htmlFor="pDorsal">{t('page.dorsal')}</Label>
                                <Input id="pDorsal" type="number" min="1" required value={playerForm.dorsal} onChange={e => setPlayerForm({ ...playerForm, dorsal: e.target.value })} />
                            </div>
                            <div className="grid gap-2 sm:col-span-2">
                                <Label htmlFor="pPosition">{t('page.corePosition')}</Label>
                                <select id="pPosition" className="rounded-md border bg-background px-3 py-2 text-sm" value={playerForm.corePosition} onChange={e => setPlayerForm({ ...playerForm, corePosition: e.target.value })}>
                                    {positionOptions.map(pos => (
                                        <option key={pos} value={pos}>{t(`page.positions.${pos}`)}</option>
                                    ))}
                                </select>
                            </div>
                            <div className="flex gap-2 sm:col-span-2">
                                <Button type="submit" disabled={savingPlayer}>
                                    {savingPlayer ? t('page.saving') : editingPlayerId ? t('page.saveChanges') : t('page.addPlayer')}
                                </Button>
                                <Button type="button" variant="outline" onClick={() => { setShowAddPlayer(false); setEditingPlayerId(null) }}>
                                    {t('page.cancel')}
                                </Button>
                            </div>
                        </form>
                    </CardContent>
                </Card>
            )}

            <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
                {team.players?.map(player => (
                    <PlayerCard
                        key={player.id}
                        player={player}
                        isOwner={isOwner}
                        onEdit={startEditPlayer}
                        onDelete={removePlayer}
                        t={t}
                    />
                ))}
            </div>

            {team.players?.length === 0 && (
                <p className="text-sm text-muted-foreground">{t('page.noPlayers')}</p>
            )}
        </div>
    )
}
