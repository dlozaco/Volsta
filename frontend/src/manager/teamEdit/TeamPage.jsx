import { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import { Card, CardContent, CardHeader, CardTitle } from '#components/ui/card'
import { Button } from '#components/ui/button'
import { useAuth } from '../../auth/AuthContext'
import { getTeamPageByName } from '@/services/team/teamApi'

const positionLabels = {
    SETTER: 'Setter',
    WING_SPIKER: 'Outside Hitter',
    MIDDLE_BLOCKER: 'Middle Blocker',
    OPPOSITE: 'Opposite',
    LIBERO: 'Libero'
}

export default function TeamPage() {
    const { token } = useAuth()
    const { name } = useParams()
    const [team, setTeam] = useState(null)
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState(null)

    useEffect(() => {
        if (!name) return
        setLoading(true)
        getTeamPageByName(name, token)
            .then(setTeam)
            .catch(err => setError(err.message))
            .finally(() => setLoading(false))
    }, [name, token])

    if (loading) {
        return <div className="flex items-center justify-center min-h-screen">Loading...</div>
    }

    if (error || !team) {
        return (
            <div className="mx-auto max-w-2xl space-y-4 rounded-lg border bg-background p-6">
                <div className="space-y-2">
                    <h1 className="text-2xl font-bold">Team not found</h1>
                    <p className="text-sm text-muted-foreground">
                        We could not load this team page.
                    </p>
                </div>
                <div className="flex gap-2">
                    <Button variant="outline" asChild>
                        <Link to="/teams">All teams</Link>
                    </Button>
                    <Button variant="outline" asChild>
                        <Link to="/">Home</Link>
                    </Button>
                </div>
            </div>
        )
    }

    return (
        <div className="space-y-6">
            <div className="flex items-center justify-between">
                <div className="space-y-1">
                    <div className="flex items-center gap-3">
                        <h1 className="text-2xl font-bold">{team.name}</h1>
                    </div>
                    {team.foundationDate && (
                        <p className="text-sm text-muted-foreground">Founded on {team.foundationDate}</p>
                    )}
                </div>
                <div className="flex gap-2">
                    <Button variant="outline" asChild>
                        <Link to="/teams">All teams</Link>
                    </Button>
                    <Button variant="outline" asChild>
                        <Link to="/">Home</Link>
                    </Button>
                </div>
            </div>

            <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
                {team.players?.map(player => (
                    <Card key={player.id} size="sm">
                        <CardHeader>
                            <CardTitle className="flex items-center gap-2">
                                <span className="flex h-8 w-8 items-center justify-center rounded-full bg-primary text-xs font-bold text-primary-foreground">
                                    {player.dorsal}
                                </span>
                                {player.name} {player.surname}
                            </CardTitle>
                        </CardHeader>
                        <CardContent className="space-y-1 text-sm">
                            <p><span className="text-muted-foreground">Position:</span> {positionLabels[player.corePosition] || player.corePosition}</p>
                            <p className="truncate"><span className="text-muted-foreground">Email:</span> {player.email}</p>
                        </CardContent>
                    </Card>
                ))}
            </div>
        </div>
    )
}