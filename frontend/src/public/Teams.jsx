import { getAllTeams } from "@/services/team/teamApi";
import { useEffect, useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "#components/ui/card";
import { Skeleton } from "#components/ui/skeleton";
import { Link } from "react-router-dom";

function TeamCard({ team }) {
  const initial = team.name?.charAt(0).toUpperCase() || "?";

  return (
    <Card className="transition-shadow hover:shadow-md">
      <CardHeader>
        <div className="flex items-center gap-3">
          <div className="flex h-12 w-12 shrink-0 items-center justify-center rounded-full bg-gradient-to-br from-primary to-primary/60 text-lg font-bold text-primary-foreground">
            {team.logoUrl ? (
              <img src={team.logoUrl} alt={team.name} className="h-full w-full rounded-full object-cover" />
            ) : (
              initial
            )}
          </div>
          <CardTitle className="text-base">{team.name}</CardTitle>
        </div>
      </CardHeader>
      <CardContent className="space-y-2 text-sm">
        {team.foundationDate && (
          <div className="flex items-center gap-2 text-muted-foreground">
            <span>Founded</span>
            <span className="font-medium text-foreground">{team.foundationDate}</span>
          </div>
        )}
        <div className="flex items-center gap-2 text-muted-foreground">
          <span>Players</span>
          <span className="font-medium text-foreground">{team.players?.length ?? 0}</span>
        </div>
      </CardContent>
    </Card>
  );
}

function TeamCardSkeleton() {
  return (
    <Card>
      <CardHeader>
        <div className="flex items-center gap-3">
          <Skeleton className="h-12 w-12 rounded-full" />
          <Skeleton className="h-5 flex-1" />
        </div>
      </CardHeader>
      <CardContent className="space-y-2">
        <Skeleton className="h-4 w-32" />
        <Skeleton className="h-4 w-24" />
      </CardContent>
    </Card>
  );
}

export default function Teams() {
  const [teams, setTeams] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    setLoading(true);
    getAllTeams()
      .then((data) => setTeams(data.content || data))
      .catch((err) => setError(err))
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return (
      <div className="space-y-6">
        <div className="space-y-1">
          <h1 className="text-2xl font-bold">Teams</h1>
          <p className="text-sm text-muted-foreground">Loading teams...</p>
        </div>
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
          {Array.from({ length: 4 }).map((_, i) => (
            <TeamCardSkeleton key={i} />
          ))}
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="mx-auto max-w-md space-y-4 rounded-lg border bg-background p-6 text-center">
        <div className="text-4xl">⚠</div>
        <h2 className="text-lg font-semibold">Could not load teams</h2>
        <p className="text-sm text-muted-foreground">{error.message || "Something went wrong"}</p>
      </div>
    );
  }

  if (teams.length === 0) {
    return (
      <div className="mx-auto max-w-md space-y-4 rounded-lg border bg-background p-6 text-center">
        <h2 className="text-lg font-semibold">No teams yet</h2>
        <p className="text-sm text-muted-foreground">There are no teams to display.</p>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="space-y-1">
        <h1 className="text-2xl font-bold">Teams</h1>
        <p className="text-sm text-muted-foreground">{teams.length} team{teams.length !== 1 ? "s" : ""}</p>
      </div>
      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
        {teams.map((team) => (
            <Link key={team.id} to={`/teams/${encodeURIComponent(team.name)}`} className="block">
                <TeamCard team={team} />
            </Link>
        ))}
      </div>
    </div>
  );
}
