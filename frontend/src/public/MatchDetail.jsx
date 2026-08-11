import { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import { Card, CardContent, CardHeader, CardTitle } from '#components/ui/card'
import { Button } from '#components/ui/button'
import { Input } from '#components/ui/input'
import { Label } from '#components/ui/label'
import { CalendarClock, MapPin, Plus, Trash2, Save, X } from 'lucide-react'
import { useTranslation } from 'react-i18next'
import { useAuth } from '../auth/AuthContext'
import { getMatch, getMatchStats, rescheduleMatch, addMatchSet, addSetParticipation, createNote, deleteNote, deleteMatch } from '@/services/match/matchApi'
import { getMyTeams, getTeamById } from '@/services/team/teamApi'

const positionOptions = ['SETTER', 'WING_SPIKER', 'MIDDLE_BLOCKER', 'OPPOSITE', 'LIBERO']

function formatDate(startMoment) {
    return new Date(startMoment).toLocaleString('en-GB', {
        weekday: 'long', day: 'numeric', month: 'long', year: 'numeric', hour: '2-digit', minute: '2-digit'
    })
}

function ScoreBadge({ match, upcomingLabel }) {
    if (!match.played) return <span className="rounded-full border px-2 py-0.5 text-xs font-medium text-muted-foreground">{upcomingLabel}</span>
    return <span className="text-3xl font-extrabold tabular-nums">{match.localScore} - {match.visitorScore}</span>
}

export default function MatchDetail() {
    const { t } = useTranslation('matches')
    const { id } = useParams()
    const { user, isAdmin, isManager } = useAuth()
    const [match, setMatch] = useState(null)
    const [stats, setStats] = useState([])
    const [myTeamIds, setMyTeamIds] = useState([])
    const [rosters, setRosters] = useState({})
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState(null)

    const [showReschedule, setShowReschedule] = useState(false)
    const [rescheduleForm, setRescheduleForm] = useState({ startMoment: '', place: '' })
    const [savingReschedule, setSavingReschedule] = useState(false)

    const [showSetForm, setShowSetForm] = useState(false)
    const [setForm, setSetForm] = useState({ setNumber: '', localTeamScore: '', visitorTeamScore: '' })
    const [savingSet, setSavingSet] = useState(false)

    const [participation, setParticipation] = useState({ setIndex: null, playerId: '', positionType: 'SETTER', points: '', faults: '' })
    const [savingParticipation, setSavingParticipation] = useState(false)

    const [noteForm, setNoteForm] = useState({ playerId: '', subject: '', description: '' })
    const [savingNote, setSavingNote] = useState(false)

    useEffect(() => {
        if (!id) return
        let cancelled = false
        getMatch(id)
            .then(m => {
                if (cancelled) return
                setMatch(m)
                return getMatchStats(id)
                    .then(s => { if (!cancelled) setStats(s) })
                    .catch(() => {})
            })
            .catch(err => { if (!cancelled) setError(err.message) })
            .finally(() => { if (!cancelled) setLoading(false) })
        return () => { cancelled = true }
    }, [id])

    useEffect(() => {
        if (!user) return
        let cancelled = false
        if (isManager) {
            getMyTeams()
                .then(ts => { if (!cancelled) setMyTeamIds(ts.map(t => t.id)) })
                .catch(() => { if (!cancelled) setMyTeamIds([]) })
        }
        return () => { cancelled = true }
    }, [user, isManager])

    const canEdit = Boolean(user) && (isAdmin || (isManager && (myTeamIds.includes(match?.localTeam?.id) || myTeamIds.includes(match?.visitorTeam?.id))))

    useEffect(() => {
        if (!match) return
        let cancelled = false
        const teamIds = [match.localTeam?.id, match.visitorTeam?.id].filter(Boolean)
        Promise.all(teamIds.map(id => getTeamById(id).catch(() => null)))
            .then(teams => {
                const byId = {}
                teams.forEach(t => { if (t) byId[t.id] = t })
                if (!cancelled) setRosters(byId)
            })
            .catch(() => {})
        return () => { cancelled = true }
    }, [match])

    if (loading) {
        return <div className="flex items-center justify-center min-h-screen">{t('detail.loading')}</div>
    }

    if (error || !match) {
        return (
            <div className="mx-auto max-w-2xl space-y-4 rounded-lg border bg-background p-6">
                <h1 className="text-2xl font-bold">{t('detail.notFound')}</h1>
                <p className="text-sm text-muted-foreground">{error || t('detail.notFoundDescription')}</p>
                <div className="flex gap-2">
                    <Button variant="outline" asChild><Link to="/matches">{t('detail.allMatches')}</Link></Button>
                    <Button variant="outline" asChild><Link to="/">{t('detail.home')}</Link></Button>
                </div>
            </div>
        )
    }

    const playerTeamNames = {}
    Object.values(rosters).forEach(team => {
        ;(team.players || []).forEach(p => { playerTeamNames[p.id] = team.name })
    })
    const canAddSet = canEdit
    const canReschedule = canEdit && !match.played

    const saveReschedule = async (e) => {
        e.preventDefault()
        setSavingReschedule(true)
        setError(null)
        try {
            const updated = await rescheduleMatch(match.id, {
                startMoment: rescheduleForm.startMoment,
                place: rescheduleForm.place || null,
            })
            setMatch(updated)
            setShowReschedule(false)
        } catch (err) {
            setError(err.message)
        } finally {
            setSavingReschedule(false)
        }
    }

    const submitSet = async (e) => {
        e.preventDefault()
        setSavingSet(true)
        setError(null)
        try {
            const updated = await addMatchSet(match.id, {
                setNumber: Number(setForm.setNumber),
                localTeamScore: Number(setForm.localTeamScore),
                visitorTeamScore: Number(setForm.visitorTeamScore),
            })
            setMatch(updated)
            setShowSetForm(false)
            setSetForm({ setNumber: String(updated.sets.length + 1), localTeamScore: '', visitorTeamScore: '' })
            setStats(await getMatchStats(match.id).catch(() => []))
        } catch (err) {
            setError(err.message)
        } finally {
            setSavingSet(false)
        }
    }

    const submitParticipation = async (e) => {
        e.preventDefault()
        setSavingParticipation(true)
        setError(null)
        try {
            await addSetParticipation(match.id, participation.setIndex, {
                playerId: Number(participation.playerId),
                positionType: participation.positionType,
                points: Number(participation.points),
                faults: Number(participation.faults),
            })
            setMatch(await getMatch(match.id))
            setStats(await getMatchStats(match.id).catch(() => []))
            setParticipation({ setIndex: null, playerId: '', positionType: 'SETTER', points: '', faults: '' })
        } catch (err) {
            setError(err.message)
        } finally {
            setSavingParticipation(false)
        }
    }

    const submitNote = async (e) => {
        e.preventDefault()
        setSavingNote(true)
        setError(null)
        try {
            const created = await createNote(match.id, {
                playerId: Number(noteForm.playerId),
                subject: noteForm.subject,
                description: noteForm.description,
            })
            setMatch(prev => ({ ...prev, notes: [...(prev.notes || []), created] }))
            setNoteForm({ playerId: '', subject: '', description: '' })
        } catch (err) {
            setError(err.message)
        } finally {
            setSavingNote(false)
        }
    }

    const removeNote = async (noteId) => {
        if (!window.confirm(t('detail.notes.deleteConfirm'))) return
        setError(null)
        try {
            await deleteNote(match.id, noteId)
            setMatch(prev => ({ ...prev, notes: (prev.notes || []).filter(n => n.id !== noteId) }))
        } catch (err) {
            setError(err.message)
        }
    }

    const removeMatch = async () => {
        if (!window.confirm(t('detail.deleteConfirm'))) return
        setError(null)
        try {
            await deleteMatch(match.id)
            window.location.href = '/matches'
        } catch (err) {
            setError(err.message)
        }
    }

    return (
        <div className="space-y-6">
            <div className="flex flex-wrap items-center justify-between gap-4">
                <div className="space-y-1">
                    <div className="flex flex-wrap items-center gap-2">
                        <h1 className="text-2xl font-bold">
                            {match.localTeam?.name} vs {match.visitorTeam?.name}
                        </h1>
                        <span className={`rounded-full px-2 py-0.5 text-xs font-medium ${match.matchType === 'LEAGUE' ? 'bg-accent text-accent-foreground' : 'bg-primary/10 text-primary'}`}>
                            {match.matchType === 'LEAGUE' ? t('detail.league') : t('detail.friendly')}
                        </span>
                    </div>
                    <p className="text-sm text-muted-foreground flex items-center gap-2">
                        <CalendarClock className="size-3" /> {formatDate(match.startMoment)}
                        {match.place ? <span className="inline-flex items-center gap-1"><MapPin className="size-3" />{match.place}</span> : null}
                    </p>
                </div>
                <div className="flex items-center gap-2">
                    <Button variant="outline" asChild><Link to="/matches">{t('detail.allMatches')}</Link></Button>
                    {canEdit && !match.played && (
                        <>
                            <Button variant="outline" size="sm" onClick={() => setShowReschedule(!showReschedule)}>
                                <CalendarClock className="size-3" /> {t('detail.reschedule')}
                            </Button>
                            <Button variant="ghost" size="sm" className="text-red-600" onClick={removeMatch}>
                                <Trash2 className="size-3" /> {t('detail.delete')}
                            </Button>
                        </>
                    )}
                </div>
            </div>

            {error && <div className="rounded-lg border border-red-200 bg-red-50 p-3 text-sm text-red-700">{error}</div>}

            <Card>
                <CardContent className="flex items-center justify-around py-6">
                    <div className="text-center">
                        <p className="font-semibold">{match.localTeam?.name}</p>
                        <p className="text-xs text-muted-foreground">{t('detail.local')}</p>
                    </div>
                    <ScoreBadge match={match} upcomingLabel={t('detail.upcoming')} />
                    <div className="text-center">
                        <p className="font-semibold">{match.visitorTeam?.name}</p>
                        <p className="text-xs text-muted-foreground">{t('detail.visitor')}</p>
                    </div>
                </CardContent>
            </Card>

            {canReschedule && showReschedule && (
                <Card>
                    <CardHeader><CardTitle className="text-base">{t('detail.rescheduleTitle')}</CardTitle></CardHeader>
                    <CardContent>
                        <form onSubmit={saveReschedule} className="grid gap-4 sm:grid-cols-2">
                            <div className="grid gap-2">
                                <Label htmlFor="rsDate">{t('detail.startDate')}</Label>
                                <Input id="rsDate" type="datetime-local" required value={rescheduleForm.startMoment} onChange={e => setRescheduleForm({ ...rescheduleForm, startMoment: e.target.value })} />
                            </div>
                            <div className="grid gap-2">
                                <Label htmlFor="rsPlace">{t('detail.place')}</Label>
                                <Input id="rsPlace" placeholder={t('detail.placePlaceholder')} value={rescheduleForm.place} onChange={e => setRescheduleForm({ ...rescheduleForm, place: e.target.value })} />
                            </div>
                            <div className="flex gap-2 sm:col-span-2">
                                <Button type="submit" disabled={savingReschedule}><Save className="size-3" /> {savingReschedule ? t('detail.saving') : t('detail.save')}</Button>
                                <Button type="button" variant="outline" onClick={() => setShowReschedule(false)}><X className="size-3" /> {t('detail.cancel')}</Button>
                            </div>
                        </form>
                    </CardContent>
                </Card>
            )}

            <div className="grid gap-6 lg:grid-cols-2">
                <Card>
                    <CardHeader className="flex-row items-center justify-between">
                        <CardTitle className="text-base">{t('detail.sets.title')}</CardTitle>
                        {canAddSet && (
                            <Button size="sm" variant="outline" onClick={() => setShowSetForm(!showSetForm)}>
                                <Plus className="size-3" /> {t('detail.sets.addSet')}
                            </Button>
                        )}
                    </CardHeader>
                    <CardContent className="space-y-3">
                        {match.sets?.length === 0 && <p className="text-sm text-muted-foreground">{t('detail.sets.noSets')}</p>}
                        {(match.sets || []).map(set => (
                            <div key={set.id} className="rounded-lg border p-3">
                                <div className="flex items-center justify-between">
                                    <span className="text-sm font-semibold">{t('detail.sets.title')} {set.setNumber}</span>
                                    <span className="font-bold tabular-nums">{set.localTeamScore} - {set.visitorTeamScore}</span>
                                </div>
                                {set.participations?.length > 0 && (
                                    <div className="mt-2 overflow-x-auto">
                                        <table className="w-full text-left text-xs">
                                            <thead>
                                                <tr className="border-b text-muted-foreground">
                                                    <th className="py-1 pr-2">{t('detail.stats.team')}</th>
                                                    <th className="py-1 pr-2">{t('detail.stats.player')}</th>
                                                    <th className="py-1 pr-2">{t('detail.stats.player') === 'Jugador' ? 'Dorsal' : 'Dorsal'}</th>
                                                    <th className="py-1 pr-2">{t('detail.stats.position')}</th>
                                                    <th className="py-1 pr-2 text-right">{t('detail.stats.points')}</th>
                                                    <th className="py-1 text-right">{t('detail.stats.faults')}</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                {set.participations.map(p => (
                                                    <tr key={p.id} className="border-b last:border-0">
                                                        <td className="py-1 pr-2 font-medium text-muted-foreground">{playerTeamNames[p.playerId] || '—'}</td>
                                                        <td className="py-1 pr-2">{p.playerName} {p.playerSurname}</td>
                                                        <td className="py-1 pr-2">{p.dorsal}</td>
                                                        <td className="py-1 pr-2">{t(`detail.positions.${p.positionType}`) || p.positionType}</td>
                                                        <td className="py-1 pr-2 text-right font-medium">{p.points}</td>
                                                        <td className="py-1 text-right">{p.faults}</td>
                                                    </tr>
                                                ))}
                                            </tbody>
                                        </table>
                                    </div>
                                )}
                                {canEdit && (
                                    <div className="mt-2 flex flex-wrap items-end gap-2">
                                        <div className="grid gap-1">
                                            <Label htmlFor={`sp-pos-${set.id}`} className="text-xs">{t('detail.participation.position')}</Label>
                                            <select
                                                id={`sp-pos-${set.id}`}
                                                className="rounded-md border bg-background px-2 py-1 text-xs"
                                                value={participation.setIndex === set.id ? participation.positionType : 'SETTER'}
                                                onChange={e => setParticipation(prev => ({ ...prev, setIndex: set.id, positionType: e.target.value }))}
                                            >
                                                {positionOptions.map(pos => <option key={pos} value={pos}>{t(`detail.positions.${pos}`)}</option>)}
                                            </select>
                                        </div>
                                        <div className="grid gap-1">
                                            <Label htmlFor={`sp-player-${set.id}`} className="text-xs">{t('detail.participation.player')}</Label>
                                            <select
                                                id={`sp-player-${set.id}`}
                                                className="rounded-md border bg-background px-2 py-1 text-xs"
                                                value={participation.setIndex === set.id ? participation.playerId : ''}
                                                onChange={e => setParticipation(prev => ({ ...prev, setIndex: set.id, playerId: e.target.value }))}
                                            >
                                                <option value="">{t('detail.participation.selectPlayer')}</option>
                                                {Object.values(rosters).map(team => (
                                                    <optgroup key={team.id} label={team.name}>
                                                        {(team.players || []).map(p => <option key={p.id} value={p.id}>{p.name} {p.surname} (#{p.dorsal})</option>)}
                                                    </optgroup>
                                                ))}
                                            </select>
                                        </div>
                                        <div className="grid gap-1">
                                            <Label htmlFor={`sp-points-${set.id}`} className="text-xs">{t('detail.participation.points')}</Label>
                                            <Input
                                                id={`sp-points-${set.id}`}
                                                type="number" min="0" className="w-20"
                                                value={participation.setIndex === set.id ? participation.points : ''}
                                                onChange={e => setParticipation(prev => ({ ...prev, setIndex: set.id, points: e.target.value }))}
                                            />
                                        </div>
                                        <div className="grid gap-1">
                                            <Label htmlFor={`sp-faults-${set.id}`} className="text-xs">{t('detail.participation.faults')}</Label>
                                            <Input
                                                id={`sp-faults-${set.id}`}
                                                type="number" min="0" className="w-20"
                                                value={participation.setIndex === set.id ? participation.faults : ''}
                                                onChange={e => setParticipation(prev => ({ ...prev, setIndex: set.id, faults: e.target.value }))}
                                            />
                                        </div>
                                        <Button
                                            size="sm"
                                            className="h-8"
                                            disabled={savingParticipation || participation.setIndex !== set.id || !participation.playerId}
                                            onClick={submitParticipation}
                                        >
                                            {t('detail.participation.add')}
                                        </Button>
                                    </div>
                                )}
                            </div>
                        ))}

                        {canAddSet && showSetForm && (
                            <form onSubmit={submitSet} className="grid gap-4 rounded-lg border border-dashed p-3 sm:grid-cols-3">
                                <div className="grid gap-1">
                                    <Label htmlFor="setNumber">{t('detail.sets.setNumber')}</Label>
                                    <Input id="setNumber" type="number" min="1" required value={setForm.setNumber} onChange={e => setSetForm({ ...setForm, setNumber: e.target.value })} />
                                </div>
                                <div className="grid gap-1">
                                    <Label htmlFor="localScore">{t('detail.sets.localScore')}</Label>
                                    <Input id="localScore" type="number" min="0" required value={setForm.localTeamScore} onChange={e => setSetForm({ ...setForm, localTeamScore: e.target.value })} />
                                </div>
                                <div className="grid gap-1">
                                    <Label htmlFor="visitorScore">{t('detail.sets.visitorScore')}</Label>
                                    <Input id="visitorScore" type="number" min="0" required value={setForm.visitorTeamScore} onChange={e => setSetForm({ ...setForm, visitorTeamScore: e.target.value })} />
                                </div>
                                <div className="flex gap-2 sm:col-span-3">
                                    <Button type="submit" size="sm" disabled={savingSet}>{savingSet ? t('detail.sets.saving') : t('detail.sets.add')}</Button>
                                    <Button type="button" size="sm" variant="outline" onClick={() => setShowSetForm(false)}>{t('detail.cancel')}</Button>
                                </div>
                            </form>
                        )}
                    </CardContent>
                </Card>

                <div className="space-y-6">
                    <Card>
                        <CardHeader><CardTitle className="text-base">{t('detail.stats.title')}</CardTitle></CardHeader>
                        <CardContent>
                            {stats.length === 0 ? (
                                <p className="text-sm text-muted-foreground">{t('detail.stats.noStats')}</p>
                            ) : (
                                <div className="overflow-x-auto">
                                    <table className="w-full text-left text-sm">
                                        <thead>
                                            <tr className="border-b text-muted-foreground text-xs">
                                                <th className="py-1 pr-2">{t('detail.stats.team')}</th>
                                                <th className="py-1 pr-2">{t('detail.stats.player')}</th>
                                                <th className="py-1 pr-2">{t('detail.stats.position')}</th>
                                                <th className="py-1 pr-2 text-right">{t('detail.stats.sets')}</th>
                                                <th className="py-1 pr-2 text-right">{t('detail.stats.points')}</th>
                                                <th className="py-1 pr-2 text-right">{t('detail.stats.faults')}</th>
                                                <th className="py-1 text-right">{t('detail.stats.avg')}</th>
                                                <th className="py-1 text-right">{t('detail.stats.eff')}</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {stats.map(s => (
                                                <tr key={s.playerId} className="border-b last:border-0">
                                                    <td className="py-1 pr-2 font-medium text-muted-foreground">{playerTeamNames[s.playerId] || '—'}</td>
                                                    <td className="py-1 pr-2 font-medium">{s.playerName} {s.playerSurname} <span className="text-muted-foreground">#{s.dorsal}</span></td>
                                                    <td className="py-1 pr-2">{t(`detail.positions.${s.corePosition}`) || s.corePosition}</td>
                                                    <td className="py-1 pr-2 text-right">{s.setsPlayed}</td>
                                                    <td className="py-1 pr-2 text-right font-bold">{s.totalPoints}</td>
                                                    <td className="py-1 pr-2 text-right">{s.totalFaults}</td>
                                                    <td className="py-1 pr-2 text-right tabular-nums">{s.averagePointsPerSet.toFixed(1)}</td>
                                                    <td className="py-1 text-right tabular-nums">{s.effectiveness.toFixed(1)}%</td>
                                                </tr>
                                            ))}
                                        </tbody>
                                    </table>
                                </div>
                            )}
                        </CardContent>
                    </Card>

                    <Card>
                        <CardHeader><CardTitle className="text-base">{t('detail.notes.title')}</CardTitle></CardHeader>
                        <CardContent className="space-y-3">
                            {match.notes?.length === 0 && <p className="text-sm text-muted-foreground">{t('detail.notes.noNotes')}</p>}
                            {(match.notes || []).map(note => (
                                <div key={note.id} className="rounded-lg border p-3">
                                    <div className="flex items-start justify-between gap-2">
                                        <div>
                                            <p className="text-sm font-semibold">{note.subject}</p>
                                            <p className="text-xs text-muted-foreground">{note.playerName || t('detail.notes.general')}</p>
                                        </div>
                                        {canEdit && (
                                            <Button variant="ghost" size="icon" className="size-7 text-red-600" onClick={() => removeNote(note.id)}>
                                                <Trash2 className="size-3" />
                                            </Button>
                                        )}
                                    </div>
                                    <p className="mt-1 text-sm">{note.description}</p>
                                </div>
                            ))}

                            {canEdit && (
                                <form onSubmit={submitNote} className="space-y-3 rounded-lg border border-dashed p-3">
                                    <div className="grid gap-1">
                                        <Label htmlFor="notePlayer">{t('detail.notes.player')}</Label>
                                        <select
                                            id="notePlayer"
                                            className="rounded-md border bg-background px-3 py-2 text-sm"
                                            required
                                            value={noteForm.playerId}
                                            onChange={e => setNoteForm({ ...noteForm, playerId: e.target.value })}
                                        >
                                            <option value="">{t('detail.notes.selectPlayer')}</option>
                                            {Object.values(rosters).map(team => (
                                                <optgroup key={team.id} label={team.name}>
                                                    {(team.players || []).map(p => <option key={p.id} value={p.id}>{p.name} {p.surname} (#{p.dorsal})</option>)}
                                                </optgroup>
                                            ))}
                                        </select>
                                    </div>
                                    <div className="grid gap-1">
                                        <Label htmlFor="noteSubject">{t('detail.notes.subject')}</Label>
                                        <Input id="noteSubject" required maxLength={256} value={noteForm.subject} onChange={e => setNoteForm({ ...noteForm, subject: e.target.value })} />
                                    </div>
                                    <div className="grid gap-1">
                                        <Label htmlFor="noteDescription">{t('detail.notes.description')}</Label>
                                        <textarea
                                            id="noteDescription"
                                            required
                                            maxLength={512}
                                            rows={3}
                                            className="rounded-md border bg-background px-3 py-2 text-sm"
                                            value={noteForm.description}
                                            onChange={e => setNoteForm({ ...noteForm, description: e.target.value })}
                                        />
                                    </div>
                                    <Button type="submit" size="sm" disabled={savingNote}>{savingNote ? t('detail.notes.saving') : t('detail.notes.addNote')}</Button>
                                </form>
                            )}
                        </CardContent>
                    </Card>
                </div>
            </div>
        </div>
    )
}
