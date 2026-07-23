import { useParams, Navigate, Link } from 'react-router-dom'
import { Card, CardContent, CardHeader, CardTitle } from '#components/ui/card'
import { Button } from '#components/ui/button'
import getIdFromUrl from '../../util/getIdFromUrl'

import { useAuth } from '../../auth/AuthContext'

const teams = [
    {
        slug: 'volsta-voley-club',
        team: {
            id: 101,
            name: 'Volsta Volleyball Club',
            foundationDate: '2018-09-15',
            logoUrl: 'https://example.com/logos/volsta.png',
            players: [
                { name: 'Ava', surname: 'Garcia', email: 'ava.garcia@volsta.com', dorsal: 7, corePosition: 'SETTER' },
                { name: 'Lily', surname: 'Martin', email: 'lily.martin@volsta.com', dorsal: 4, corePosition: 'WING_SPIKER' },
                { name: 'Chloe', surname: 'Santos', email: 'chloe.santos@volsta.com', dorsal: 12, corePosition: 'MIDDLE_BLOCKER' },
                { name: 'Mia', surname: 'Lopez', email: 'mia.lopez@volsta.com', dorsal: 9, corePosition: 'OPPOSITE' },
                { name: 'Emma', surname: 'Ruiz', email: 'emma.ruiz@volsta.com', dorsal: 1, corePosition: 'LIBERO' },
                { name: 'Olivia', surname: 'Perez', email: 'olivia.perez@volsta.com', dorsal: 2, corePosition: 'SETTER' },
                { name: 'Sophia', surname: 'Navarro', email: 'sophia.navarro@volsta.com', dorsal: 3, corePosition: 'WING_SPIKER' },
                { name: 'Maya', surname: 'Gil', email: 'maya.gil@volsta.com', dorsal: 5, corePosition: 'MIDDLE_BLOCKER' },
                { name: 'Grace', surname: 'Vega', email: 'grace.vega@volsta.com', dorsal: 6, corePosition: 'OPPOSITE' },
                { name: 'Ella', surname: 'Torres', email: 'ella.torres@volsta.com', dorsal: 8, corePosition: 'LIBERO' },
                { name: 'Isabella', surname: 'Molina', email: 'isabella.molina@volsta.com', dorsal: 10, corePosition: 'SETTER' },
                { name: 'Harper', surname: 'Ortega', email: 'harper.ortega@volsta.com', dorsal: 11, corePosition: 'WING_SPIKER' },
                { name: 'Zoe', surname: 'Ramos', email: 'zoe.ramos@volsta.com', dorsal: 13, corePosition: 'MIDDLE_BLOCKER' },
                { name: 'Ella', surname: 'Castro', email: 'ella.castro@volsta.com', dorsal: 14, corePosition: 'OPPOSITE' }
            ]
        }
    }
]

const positionLabels = {
    SETTER: 'Setter',
    WING_SPIKER: 'Outside Hitter',
    MIDDLE_BLOCKER: 'Middle Blocker',
    OPPOSITE: 'Opposite',
    LIBERO: 'Libero'
} 

export default function TeamPage() {
    const { user, loading } = useAuth()
    const nameFromUrl = getIdFromUrl(2)


    const { name } = useParams()
    const entry = teams.find(t => t.slug === name)

    if (!entry) {
        return <Navigate to="/" replace />
    }

    const { team } = entry

    return (
        <div className="space-y-6">
            <div className="flex items-center justify-between">
                <div className="space-y-1">
                    <div className="flex items-center gap-3">
                        <h1 className="text-2xl font-bold">{team.name}</h1>
                        <span className={`inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium ${loading ? 'bg-muted text-muted-foreground' : user ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}`}>
                            {loading ? 'Checking...' : user ? `Logged in as ${user.username || user.email || 'user'}` : 'Not logged in'}
                        </span>
                    </div>
                    <p className="text-sm text-muted-foreground">Founded on {team.foundationDate}</p>
                </div>
                <Button variant="outline" asChild>
                    <Link to="/">Back to home</Link>
                </Button>
            </div>

            <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
                {team.players.map(player => (
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
                            <p><span className="text-muted-foreground">Position:</span> {positionLabels[player.corePosition]}</p>
                            <p className="truncate"><span className="text-muted-foreground">Email:</span> {player.email}</p>
                        </CardContent>
                    </Card>
                ))}
            </div>
        </div>
    )
}