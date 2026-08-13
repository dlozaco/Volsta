import { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import { Card, CardContent, CardHeader, CardTitle } from '#components/ui/card'
import { Button } from '#components/ui/button'
import { Skeleton } from '#components/ui/skeleton'
import { ArrowLeft, Trophy, TrendingUp } from 'lucide-react'
import { useTranslation } from 'react-i18next'
import { getTeamByName, getTeamStats } from '@/services/team/teamApi'

const positionKeys = ['SETTER', 'MIDDLE_BLOCKER', 'WING_SPIKER', 'OPPOSITE', 'LIBERO']

function SummaryCard({ label, value, highlight }) {
    return (
        <Card size="sm">
            <CardHeader>
                <CardTitle className="text-sm text-muted-foreground">{label}</CardTitle>
            </CardHeader>
            <CardContent>
                <span className={`text-3xl font-extrabold tabular-nums ${highlight ? 'text-primary' : ''}`}>
                    {value}
                </span>
            </CardContent>
        </Card>
    )
}

function PlayerTable({ players, t }) {
    if (players.length === 0) {
        return <p className="text-sm text-muted-foreground">{t('playerTable.noPlayers')}</p>
    }
    return (
        <div className="overflow-x-auto">
            <table className="w-full text-left text-sm">
                <thead>
                    <tr className="border-b text-muted-foreground text-xs">
                        <th className="py-1 pr-2">{t('playerTable.player')}</th>
                        <th className="py-1 pr-2">{t('playerTable.position')}</th>
                        <th className="py-1 pr-2 text-right">{t('playerTable.sets')}</th>
                        <th className="py-1 pr-2 text-right">{t('playerTable.points')}</th>
                        <th className="py-1 pr-2 text-right">{t('playerTable.faults')}</th>
                        <th className="py-1 text-right">{t('playerTable.avg')}</th>
                        <th className="py-1 text-right">{t('playerTable.eff')}</th>
                    </tr>
                </thead>
                <tbody>
                    {players.map(p => (
                        <tr key={p.playerId} className="border-b last:border-0">
                            <td className="py-1 pr-2 font-medium">
                                {p.playerName} {p.playerSurname} <span className="text-muted-foreground">#{p.dorsal}</span>
                            </td>
                            <td className="py-1 pr-2">{t(`positions.${p.corePosition}`) || p.corePosition}</td>
                            <td className="py-1 pr-2 text-right">{p.setsPlayed}</td>
                            <td className="py-1 pr-2 text-right font-bold">{p.totalPoints}</td>
                            <td className="py-1 pr-2 text-right">{p.totalFaults}</td>
                            <td className="py-1 pr-2 text-right tabular-nums">{p.averagePointsPerSet.toFixed(1)}</td>
                            <td className="py-1 text-right tabular-nums">{p.effectiveness.toFixed(1)}%</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    )
}

function StatsSkeleton() {
    return (
        <div className="space-y-6">
            <Skeleton className="h-8 w-64" />
            <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
                {Array.from({ length: 4 }).map((_, i) => (
                    <Card key={i} size="sm"><CardContent><Skeleton className="h-10 w-16" /></CardContent></Card>
                ))}
            </div>
            <Skeleton className="h-48 w-full" />
        </div>
    )
}

export default function TeamStats() {
    const { t } = useTranslation('teamStats')
    const { name } = useParams()
    const [stats, setStats] = useState(null)
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState(null)

    useEffect(() => {
        if (!name) return
        let cancelled = false
        getTeamByName(name)
            .then(team => getTeamStats(team.id))
            .then(data => { if (!cancelled) setStats(data) })
            .catch(err => { if (!cancelled) setError(err.message) })
            .finally(() => { if (!cancelled) setLoading(false) })
        return () => { cancelled = true }
    }, [name])

    if (loading) {
        return <StatsSkeleton />
    }

    if (error || !stats) {
        return (
            <div className="mx-auto max-w-2xl space-y-4 rounded-lg border bg-background p-6">
                <h1 className="text-2xl font-bold">{t('notFound')}</h1>
                <p className="text-sm text-muted-foreground">{error || ''}</p>
                <div className="flex gap-2">
                    <Button variant="outline" asChild>
                        <Link to="/teams">{t('allTeams')}</Link>
                    </Button>
                </div>
            </div>
        )
    }

    const topScorersByPosition = positionKeys
        .map(pos => ({ pos, scorer: stats.topScorerByPosition?.[pos] }))
        .filter(entry => entry.scorer)

    return (
        <div className="space-y-6">
            <div className="flex flex-wrap items-center justify-between gap-4">
                <div className="space-y-1">
                    <h1 className="text-2xl font-bold">{t('title')}</h1>
                    <p className="text-sm text-muted-foreground">{t('subtitle', { teamName: stats.teamName })}</p>
                </div>
                <Button variant="outline" asChild>
                    <Link to={`/teams/${encodeURIComponent(stats.teamName)}`}>
                        <ArrowLeft className="size-3" /> {t('back')}
                    </Link>
                </Button>
            </div>

            <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
                <SummaryCard label={t('summary.played')} value={stats.matchesPlayed} />
                <SummaryCard label={t('summary.won')} value={stats.matchesWon} highlight />
                <SummaryCard label={t('summary.lost')} value={stats.matchesLost} />
                <SummaryCard
                    label={t('summary.winRate')}
                    value={stats.matchesPlayed === 0 ? '0%' : `${Math.round((stats.matchesWon / stats.matchesPlayed) * 100)}%`}
                    highlight
                />
            </div>

            <div className="grid gap-6 lg:grid-cols-2">
                <Card>
                    <CardHeader>
                        <CardTitle className="flex items-center gap-2 text-base">
                            <Trophy className="size-4 text-primary" /> {t('topScorer.title')}
                        </CardTitle>
                    </CardHeader>
                    <CardContent>
                        {stats.topScorer ? (
                            <div className="flex flex-wrap items-center gap-3">
                                <span className="flex h-10 w-10 items-center justify-center rounded-full bg-primary text-sm font-bold text-primary-foreground">
                                    {stats.topScorer.dorsal}
                                </span>
                                <div>
                                    <p className="font-semibold">
                                        {stats.topScorer.playerName} {stats.topScorer.playerSurname}
                                    </p>
                                    <p className="text-sm text-muted-foreground">
                                        {t(`positions.${stats.topScorer.position}`) || stats.topScorer.position}
                                    </p>
                                </div>
                                <span className="ml-auto text-2xl font-extrabold text-primary tabular-nums">
                                    {stats.topScorer.totalPoints} <span className="text-sm font-medium text-muted-foreground">{t('topScorer.points')}</span>
                                </span>
                            </div>
                        ) : (
                            <p className="text-sm text-muted-foreground">{t('empty')}</p>
                        )}
                    </CardContent>
                </Card>

                <Card>
                    <CardHeader>
                        <CardTitle className="flex items-center gap-2 text-base">
                            <TrendingUp className="size-4 text-primary" /> {t('topScorerByPosition.title')}
                        </CardTitle>
                    </CardHeader>
                    <CardContent className="space-y-3">
                        {topScorersByPosition.length === 0 && (
                            <p className="text-sm text-muted-foreground">{t('empty')}</p>
                        )}
                        {topScorersByPosition.map(({ pos, scorer }) => (
                            <div key={pos} className="flex items-center justify-between gap-2 rounded-lg border p-3">
                                <span className="text-sm font-medium">{t(`positions.${pos}`)}</span>
                                <div className="flex items-center gap-2 text-sm">
                                    <span className="font-medium">{scorer.playerName} {scorer.playerSurname}</span>
                                    <span className="text-muted-foreground">#{scorer.dorsal}</span>
                                    <span className="font-bold text-primary tabular-nums">{scorer.totalPoints}</span>
                                </div>
                            </div>
                        ))}
                    </CardContent>
                </Card>
            </div>

            <Card>
                <CardHeader>
                    <CardTitle className="text-base">{t('playerTable.title')}</CardTitle>
                </CardHeader>
                <CardContent>
                    <PlayerTable players={stats.players || []} t={t} />
                </CardContent>
            </Card>
        </div>
    )
}