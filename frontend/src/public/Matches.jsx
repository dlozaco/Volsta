import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { Card, CardContent } from '#components/ui/card'
import { Button } from '#components/ui/button'
import { CalendarPlus, MapPin } from 'lucide-react'
import { useTranslation } from 'react-i18next'
import { useAuth } from '../auth/AuthContext'
import { getAllMatches } from '@/services/match/matchApi'

function formatDate(startMoment) {
    return new Date(startMoment).toLocaleString('en-GB', {
        weekday: 'short', day: 'numeric', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit'
    })
}

function MatchCard({ match, t }) {
    return (
        <Link to={`/matches/${match.id}`} className="block">
            <Card className="transition-shadow hover:shadow-md">
                <CardContent className="flex flex-wrap items-center justify-between gap-4 py-3">
                    <div className="min-w-0">
                        <div className="flex flex-wrap items-center gap-2">
                            <span className={`rounded-full px-2 py-0.5 text-xs font-medium ${match.matchType === 'LEAGUE' ? 'bg-accent text-accent-foreground' : 'bg-primary/10 text-primary'}`}>
                                {match.matchType === 'LEAGUE' ? t('list.filters.league') : t('list.filters.friendly')}
                            </span>
                            {match.played && (
                                <span className="rounded-full bg-green-100 px-2 py-0.5 text-xs font-medium text-green-700">{t('detail.played')}</span>
                            )}
                        </div>
                        <p className="mt-1 truncate font-semibold">
                            {match.localTeam?.name} <span className="text-muted-foreground">vs</span> {match.visitorTeam?.name}
                        </p>
                        <p className="text-xs text-muted-foreground">
                            {formatDate(match.startMoment)}
                            {match.place ? <span className="inline-flex items-center gap-1"><MapPin className="size-3" />{match.place}</span> : null}
                        </p>
                    </div>
                    <div className="text-right">
                        {match.played ? (
                            <p className="text-xl font-extrabold tabular-nums">
                                {match.localScore} <span className="text-muted-foreground">-</span> {match.visitorScore}
                            </p>
                        ) : (
                            <span className="rounded-full border px-2 py-0.5 text-xs font-medium text-muted-foreground">{t('detail.upcoming')}</span>
                        )}
                    </div>
                </CardContent>
            </Card>
        </Link>
    )
}

export default function Matches() {
    const { t } = useTranslation('matches')
    const { user } = useAuth()
    const [matches, setMatches] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState(null)
    const [typeFilter, setTypeFilter] = useState('ALL')

    useEffect(() => {
        let cancelled = false
        getAllMatches()
            .then(data => { if (!cancelled) setMatches(data) })
            .catch(err => { if (!cancelled) setError(err.message) })
            .finally(() => { if (!cancelled) setLoading(false) })
        return () => { cancelled = true }
    }, [])

    const sorted = [...matches].sort((a, b) => new Date(a.startMoment) - new Date(b.startMoment))
    const filtered = typeFilter === 'ALL' ? sorted : sorted.filter(m => m.matchType === typeFilter)

    if (loading) {
        return <div className="flex items-center justify-center min-h-screen">{t('list.loading')}</div>
    }

    if (error) {
        return (
            <div className="mx-auto max-w-md space-y-4 rounded-lg border bg-background p-6 text-center">
                <h2 className="text-lg font-semibold">{t('list.error.title')}</h2>
                <p className="text-sm text-muted-foreground">{error}</p>
                <Button variant="outline" asChild><Link to="/">{t('detail.home')}</Link></Button>
            </div>
        )
    }

    return (
        <div className="space-y-6">
            <div className="flex flex-wrap items-center justify-between gap-4">
                <div className="space-y-1">
                    <h1 className="text-2xl font-bold">{t('list.title')}</h1>
                    <p className="text-sm text-muted-foreground">{t('list.subtitle', { count: filtered.length })}</p>
                </div>
                <div className="flex items-center gap-2">
                    <div className="flex gap-1">
                        {['ALL', 'LEAGUE', 'FRIENDLY'].map(f => (
                            <Button
                                key={f}
                                size="sm"
                                variant={typeFilter === f ? 'default' : 'outline'}
                                onClick={() => setTypeFilter(f)}
                            >
                                {f === 'ALL' ? t('list.filters.all') : f === 'LEAGUE' ? t('list.filters.league') : t('list.filters.friendly')}
                            </Button>
                        ))}
                    </div>
                    {user && (
                        <Button size="sm" asChild>
                            <Link to="/matches/new"><CalendarPlus className="size-3" /> {t('list.newMatch')}</Link>
                        </Button>
                    )}
                </div>
            </div>

            {filtered.length === 0 ? (
                <p className="text-sm text-muted-foreground">{t('list.noMatches')}</p>
            ) : (
                <div className="grid gap-3">
                    {filtered.map(m => <MatchCard key={m.id} match={m} t={t} />)}
                </div>
            )}
        </div>
    )
}
