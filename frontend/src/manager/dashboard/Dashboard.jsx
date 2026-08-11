import { useEffect, useMemo, useState } from 'react'
import { Link } from 'react-router-dom'
import { Card, CardContent, CardHeader, CardTitle } from '#components/ui/card'
import { Button } from '#components/ui/button'
import { ChevronLeft, ChevronRight, CalendarPlus, Clock } from 'lucide-react'
import { useAuth } from '../../auth/AuthContext'
import { getMyTeams } from '@/services/team/teamApi'
import { getMatchesByTeam, getAllMatches } from '@/services/match/matchApi'
import { useTranslation } from 'react-i18next'

const DAY_KEYS = ['mon', 'tue', 'wed', 'thu', 'fri', 'sat', 'sun']

function matchDate(match) {
    return new Date(match.startMoment).toLocaleDateString('en-CA') // YYYY-MM-DD
}

function dayMatches(matches, dateKey) {
    return matches.filter(m => matchDate(m) === dateKey)
}

function formatCountdown(target, now) {
    const diff = target.getTime() - now
    if (diff <= 0) return 'Started'
    const days = Math.floor(diff / (1000 * 60 * 60 * 24))
    const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
    const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))
    if (days > 0) return `${days}d ${hours}h`
    if (hours > 0) return `${hours}h ${minutes}m`
    return `${minutes}m`
}

export default function Dashboard() {
    const { isManager, isAdmin } = useAuth()
    const [team, setTeam] = useState(null)
    const [matches, setMatches] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState(null)
    const [now, setNow] = useState(() => Date.now())
    const [month, setMonth] = useState(() => {
        const now = new Date()
        return new Date(now.getFullYear(), now.getMonth(), 1)
    })
    const { t } = useTranslation('dashboard')

    useEffect(() => {
        let cancelled = false

        const load = async () => {
            try {
                let myTeams = []
                if (isManager) {
                    myTeams = await getMyTeams()
                    setTeam(myTeams[0] || null)
                }
                let data
                if (isManager && myTeams.length) {
                    const perTeam = await Promise.all(myTeams.map(t => getMatchesByTeam(t.id).catch(() => [])))
                    data = perTeam.flat()
                } else {
                    data = await getAllMatches()
                }
                if (!cancelled) setMatches(data)
            } catch (err) {
                if (!cancelled) setError(err.message)
            } finally {
                if (!cancelled) setLoading(false)
            }
        }

        load()
        return () => { cancelled = true }
    }, [isManager, isAdmin])

    useEffect(() => {
        const id = setInterval(() => setNow(Date.now()), 30000)
        return () => clearInterval(id)
    }, [])

    const upcoming = matches
        .filter(m => new Date(m.startMoment).getTime() > now)
        .sort((a, b) => new Date(a.startMoment) - new Date(b.startMoment))

    const nextMatch = upcoming[0]

    const calendarCells = useMemo(() => {
        const year = month.getFullYear()
        const monthIndex = month.getMonth()
        const firstWeekday = new Date(year, monthIndex, 1).getDay() // 0=Sun
        const leading = (firstWeekday + 6) % 7 // Monday-first
        const daysInMonth = new Date(year, monthIndex + 1, 0).getDate()

        const cells = []
        for (let i = 0; i < leading; i++) cells.push(null)
        for (let d = 1; d <= daysInMonth; d++) cells.push(d)
        return cells
    }, [month])

    const changeMonth = (delta) => {
        setMonth(prev => new Date(prev.getFullYear(), prev.getMonth() + delta, 1))
    }

    const monthLabel = month.toLocaleDateString('en-US', { month: 'long', year: 'numeric' })


    if (loading) {
        return <div className="flex items-center justify-center min-h-screen">{t('loading')}</div>
    }

    return (
        <div className="space-y-6">
            <div className="flex flex-wrap items-center justify-between gap-4">
                <div className="space-y-1">
                    <h1 className="text-2xl font-bold">{t('header.title')}</h1>
                    <p className="text-sm text-muted-foreground">
                        {isManager && team ? t('header.subtitle', { teamName: team.name }) : 'League calendar'}
                    </p>
                </div>
                <div className="flex gap-2">
                    <Button variant="outline" asChild>
                        <Link to="/matches/new">
                            <CalendarPlus className="size-3" /> {t('buttons.newMatch')}
                        </Link>
                    </Button>
                    <Button variant="outline" asChild>
                        <Link to="/matches">{t('buttons.allMatches')}</Link>
                    </Button>
                </div>
            </div>

            {error && (
                <div className="rounded-lg border border-red-200 bg-red-50 p-3 text-sm text-red-700">{error}</div>
            )}

            {nextMatch && (
                <Card>
                    <CardHeader>
                        <CardTitle className="flex items-center gap-2 text-base">
                            <Clock className="size-4 text-primary" /> {t('upcomingMatches.countdownTitle')}
                        </CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="flex flex-wrap items-center justify-between gap-4">
                            <div>
                                <p className="font-semibold">
                                    {nextMatch.localTeam?.name} vs {nextMatch.visitorTeam?.name}
                                </p>
                                <p className="text-sm text-muted-foreground">
                                    {new Date(nextMatch.startMoment).toLocaleString('en-GB', {
                                        weekday: 'long', day: 'numeric', month: 'long', hour: '2-digit', minute: '2-digit'
                                    })}
                                    {nextMatch.place ? ` · ${nextMatch.place}` : ''}
                                </p>
                            </div>
                            <div className="text-right">
                                <div className="text-3xl font-extrabold text-primary tabular-nums">
                                    {formatCountdown(new Date(nextMatch.startMoment), now)}
                                </div>
                                <div className="text-xs text-muted-foreground">{t('upcomingMatches.untilKickoff')}</div>
                            </div>
                        </div>
                    </CardContent>
                </Card>
            )}

            <Card>
                <CardHeader className="flex-row items-center justify-between">
                    <CardTitle className="text-base">{t('calendar.title')}</CardTitle>
                    <div className="flex items-center gap-2">
                        <Button variant="outline" size="icon" onClick={() => changeMonth(-1)} aria-label="Previous month">
                            <ChevronLeft className="size-4" />
                        </Button>
                        <span className="w-36 text-center text-sm font-semibold">{monthLabel}</span>
                        <Button variant="outline" size="icon" onClick={() => changeMonth(1)} aria-label="Next month">
                            <ChevronRight className="size-4" />
                        </Button>
                    </div>
                </CardHeader>
                <CardContent>
                    <div className="grid grid-cols-7 gap-1 text-center">
                        {DAY_KEYS.map(dayKey => (
                            <div key={dayKey} className="py-1 text-xs font-medium text-muted-foreground">
                                {t(`calendar.days.${dayKey}`)}
                            </div>
                        ))}
                        {calendarCells.map((day, i) => {
                            if (day === null) return <div key={`empty-${i}`} />
                            const dateKey = `${month.getFullYear()}-${String(month.getMonth() + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`
                            const dayMatchList = dayMatches(matches, dateKey)
                            const hasMatch = dayMatchList.length > 0
                            const isToday = dateKey === new Date().toLocaleDateString('en-CA')
                            return (
                                <div
                                    key={dateKey}
                                    className={`relative rounded-lg border p-2 text-sm min-h-14 ${hasMatch ? 'border-primary bg-primary/10 font-medium' : 'border-border'} ${isToday ? 'ring-2 ring-primary/40' : ''}`}
                                >
                                    <span>{day}</span>
                                    <div className="mt-1 space-y-1">
                                        {dayMatchList.map(m => (
                                            <Link
                                                key={m.id}
                                                to={`/matches/${m.id}`}
                                                className="block truncate rounded bg-primary text-[10px] text-primary-foreground px-1 py-0.5"
                                            >
                                                {m.localTeam?.name} - {m.visitorTeam?.name}
                                            </Link>
                                        ))}
                                    </div>
                                </div>
                            )
                        })}
                    </div>
                </CardContent>
            </Card>

            <div className="space-y-2">
                <h2 className="text-lg font-semibold">{t('upcomingMatches.title')}</h2>
                {upcoming.length === 0 ? (
                    <p className="text-sm text-muted-foreground">{t('upcomingMatches.emptyState')}</p>
                ) : (
                    <div className="grid gap-3 sm:grid-cols-2">
                        {upcoming.map(m => (
                            <Link key={m.id} to={`/matches/${m.id}`} className="block">
                                <Card className="transition-shadow hover:shadow-md">
                                    <CardContent className="flex items-center justify-between py-3">
                                        <div>
                                            <p className="font-semibold text-sm">{m.localTeam?.name} vs {m.visitorTeam?.name}</p>
                                            <p className="text-xs text-muted-foreground">
                                                {new Date(m.startMoment).toLocaleString('en-GB', {
                                                    day: 'numeric', month: 'short', hour: '2-digit', minute: '2-digit'
                                                })}
                                                {m.place ? ` · ${m.place}` : ''}
                                            </p>
                                        </div>
                                        <span className="text-xs font-medium text-primary">{formatCountdown(new Date(m.startMoment), now)}</span>
                                    </CardContent>
                                </Card>
                            </Link>
                        ))}
                    </div>
                )}
            </div>
        </div>
    )
}
