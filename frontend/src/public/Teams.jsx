import { getAllTeams } from "@/services/team/teamApi";
import { useEffect, useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "#components/ui/card";
import { Skeleton } from "#components/ui/skeleton";
import { Button } from "#components/ui/button";
import { Link } from "react-router-dom";
import { Plus } from "lucide-react";
import { useAuth } from "../auth/AuthContext";
import { useTranslation } from "react-i18next";

function TeamCard({ team }) {
  const initial = team.name?.charAt(0).toUpperCase() || "?";
  const { t } = useTranslation('teams')

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
            <span>{t('founded')}</span>
            <span className="font-medium text-foreground">{team.foundationDate}</span>
          </div>
        )}
        <div className="flex items-center gap-2 text-muted-foreground">
          <span>{t('players')}</span>
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
  const { user, isManager } = useAuth();
  const [teams, setTeams] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const { t } = useTranslation('teams')

  useEffect(() => {
    let cancelled = false
    getAllTeams()
      .then(data => { if (!cancelled) setTeams(data) })
      .catch(err => { if (!cancelled) setError(err) })
      .finally(() => { if (!cancelled) setLoading(false) })
    return () => { cancelled = true }
  }, [])

  if (loading) {
    return (
      <div className="space-y-6">
        <div className="space-y-1">
          <h1 className="text-2xl font-bold">{t('title')}</h1>
          <p className="text-sm text-muted-foreground">{t('loading')}</p>
        </div>
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
          {Array.from({ length: 8 }).map((_, i) => (
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
        <h2 className="text-lg font-semibold">{t('error.notLoad')}</h2>
        <p className="text-sm text-muted-foreground">{error.message || "Something went wrong"}</p>
      </div>
    );
  }

  if (teams.length === 0) {
    return (
      <div className="mx-auto max-w-md space-y-4 rounded-lg border bg-background p-6 text-center">
        <h2 className="text-lg font-semibold">{t('error.notTeams.title')}</h2>
        <p className="text-sm text-muted-foreground">{t('error.notTeams.subtitle')}</p>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-center justify-between gap-4">
        <div className="space-y-1">
          <h1 className="text-2xl font-bold">{t('title')}</h1>
          <p className="text-sm text-muted-foreground">{teams.length} {t('subtitle')}{teams.length !== 1 ? 's' : ''}</p>
        </div>
        {user && isManager && (
          <Button size="sm" asChild>
            <Link to="/teams/new"><Plus className="size-3" /> {t('create')}</Link>
          </Button>
        )}
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
