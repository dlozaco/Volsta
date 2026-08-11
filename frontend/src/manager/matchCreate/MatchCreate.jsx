import { useEffect, useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { Card, CardContent, CardHeader, CardTitle } from '#components/ui/card'
import { Button } from '#components/ui/button'
import { Input } from '#components/ui/input'
import { Label } from '#components/ui/label'
import { CalendarPlus } from 'lucide-react'
import { useTranslation } from 'react-i18next'
import { useAuth } from '../../auth/AuthContext'
import { getAllTeams, getMyTeams } from '@/services/team/teamApi'
import { createMatch } from '@/services/match/matchApi'

export default function MatchCreate() {
    const { t } = useTranslation('matches')
    const { user, isAdmin, isManager } = useAuth()
    const navigate = useNavigate()
    const [teams, setTeams] = useState([])
    const [loadingTeams, setLoadingTeams] = useState(true)
    const [form, setForm] = useState({ localTeamId: '', visitorTeamId: '', startMoment: '', place: '', matchType: 'FRIENDLY' })
    const [saving, setSaving] = useState(false)
    const [error, setError] = useState(null)

    useEffect(() => {
        let cancelled = false
        Promise.all([
            getAllTeams().catch(() => []),
            isManager ? getMyTeams().catch(() => []) : Promise.resolve([]),
        ])
            .then(([allTeams, myTeams]) => {
                if (cancelled) return
                setTeams(allTeams)
                if (isManager && !isAdmin && myTeams.length) {
                    setForm(prev => ({ ...prev, localTeamId: String(myTeams[0].id) }))
                }
            })
            .finally(() => { if (!cancelled) setLoadingTeams(false) })
        return () => { cancelled = true }
    }, [isManager, isAdmin])

    const canCreateLeague = isAdmin

    const submit = async (e) => {
        e.preventDefault()
        setSaving(true)
        setError(null)
        try {
            const created = await createMatch({
                localTeamId: Number(form.localTeamId),
                visitorTeamId: Number(form.visitorTeamId),
                startMoment: form.startMoment,
                place: form.place || null,
                matchType: form.matchType,
            })
            navigate(`/matches/${created.id}`)
        } catch (err) {
            setError(err.message)
        } finally {
            setSaving(false)
        }
    }

    if (!user) {
        return (
            <div className="mx-auto max-w-md space-y-4 rounded-lg border bg-background p-6 text-center">
                <h1 className="text-2xl font-bold">{t('create.signinRequired')}</h1>
                <p className="text-sm text-muted-foreground">{t('create.signinDescription')}</p>
                <Button asChild><Link to="/login">{t('create.login')}</Link></Button>
            </div>
        )
    }

    if (loadingTeams) {
        return <div className="flex items-center justify-center min-h-screen">{t('create.loadingTeams')}</div>
    }

    return (
        <div className="mx-auto max-w-2xl space-y-6">
            <div className="space-y-1">
                <h1 className="text-2xl font-bold">{t('create.title')}</h1>
                <p className="text-sm text-muted-foreground">
                    {isManager && !isAdmin
                        ? t('create.descriptionManager')
                        : t('create.descriptionAdmin')}
                </p>
            </div>

            {error && <div className="rounded-lg border border-red-200 bg-red-50 p-3 text-sm text-red-700">{error}</div>}

            <Card>
                <CardHeader><CardTitle className="flex items-center gap-2 text-base"><CalendarPlus className="size-4 text-primary" /> {t('create.details')}</CardTitle></CardHeader>
                <CardContent>
                    <form onSubmit={submit} className="grid gap-4 sm:grid-cols-2">
                        <div className="grid gap-2">
                            <Label htmlFor="matchType">{t('create.matchType')}</Label>
                            <select
                                id="matchType"
                                className="rounded-md border bg-background px-3 py-2 text-sm"
                                value={form.matchType}
                                onChange={e => setForm(prev => ({ ...prev, matchType: e.target.value }))}
                            >
                                <option value="FRIENDLY">{t('create.friendly')}</option>
                                {canCreateLeague && <option value="LEAGUE">{t('create.league')}</option>}
                            </select>
                        </div>
                        <div className="grid gap-2">
                            <Label htmlFor="startMoment">{t('create.startDate')}</Label>
                            <Input id="startMoment" type="datetime-local" required value={form.startMoment} onChange={e => setForm({ ...form, startMoment: e.target.value })} />
                        </div>
                        <div className="grid gap-2">
                            <Label htmlFor="localTeam">{t('create.localTeam')}</Label>
                            <select
                                id="localTeam"
                                className="rounded-md border bg-background px-3 py-2 text-sm"
                                required
                                value={form.localTeamId}
                                disabled={isManager && !isAdmin}
                                onChange={e => setForm({ ...form, localTeamId: e.target.value })}
                            >
                                <option value="">{t('create.selectLocalTeam')}</option>
                                {teams.map(t => <option key={t.id} value={t.id}>{t.name}</option>)}
                            </select>
                        </div>
                        <div className="grid gap-2">
                            <Label htmlFor="visitorTeam">{t('create.visitorTeam')}</Label>
                            <select
                                id="visitorTeam"
                                className="rounded-md border bg-background px-3 py-2 text-sm"
                                required
                                value={form.visitorTeamId}
                                onChange={e => setForm({ ...form, visitorTeamId: e.target.value })}
                            >
                                <option value="">{t('create.selectVisitorTeam')}</option>
                                {teams.filter(t => String(t.id) !== String(form.localTeamId)).map(t => <option key={t.id} value={t.id}>{t.name}</option>)}
                            </select>
                        </div>
                        <div className="grid gap-2 sm:col-span-2">
                            <Label htmlFor="place">{t('create.place')}</Label>
                            <Input id="place" placeholder={t('create.placePlaceholder')} value={form.place} onChange={e => setForm({ ...form, place: e.target.value })} />
                        </div>
                        <div className="flex gap-2 sm:col-span-2">
                            <Button type="submit" disabled={saving}>{saving ? t('create.creating') : t('create.createMatch')}</Button>
                            <Button type="button" variant="outline" asChild><Link to="/matches">{t('create.cancel')}</Link></Button>
                        </div>
                    </form>
                </CardContent>
            </Card>
        </div>
    )
}
