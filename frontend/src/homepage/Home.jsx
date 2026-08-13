
import { Volleyball, Users, TrendingUp, Calendar, LogIn, UserPlus, Shield } from "lucide-react"
import { Link } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";
import { useTranslation } from "react-i18next";


export default function Home() {
    const { user } = useAuth()
    const { t } = useTranslation('home')


    return (
        <div className="relative flex flex-col items-center justify-center min-h-[calc(100vh-64px)] text-center p-6 bg-background">
            <div className="absolute inset-0 bg-linear-to-br from-background via-slate-950/20 to-background opacity-70 z-0" />
            
                <div className="relative z-10 flex flex-col items-center">
                    <h1 className="text-7xl font-extrabold tracking-tighter text-foreground md:text-8xl flex items-center justify-center gap-4">
                        <Volleyball className="w-14 h-14 md:w-20 md:h-20 text-primary shrink-0" />
                            Volsta
                    </h1>
                    
                    <p className="mt-5 text-2xl font-medium tracking-tight text-muted-foreground max-w-150 md:text-3xl">
                        {t('welcome')}
                    </p>

                    <div className="mt-12 flex flex-wrap gap-4 justify-center">
                    {user ? (
                        <Link 
                            to={'/teams'}
                            className="inline-flex items-center gap-2 px-6 py-3 bg-primary text-primary-foreground font-semibold rounded-full shadow-lg hover:bg-primary/90 transition-colors">
                            <Shield/>
                            {t('buttons.main')}
                        </Link>
                    ) : (
                        <>
                            <Link 
                                to={'/register'}
                                className="inline-flex items-center gap-2 px-6 py-3 bg-primary text-primary-foreground font-semibold rounded-full shadow-lg hover:bg-primary/90 transition-colors">
                                <UserPlus/>
                                {t('buttons.signUp')}
                            </Link>
                            <Link
                                to={'/login'}
                                className="inline-flex items-center gap-2 px-6 py-3 bg-secondary text-secondary-foreground font-semibold rounded-full shadow-lg hover:bg-primary/90 transition-all duration-200"
                                >
                                <LogIn/>
                                {t('buttons.login')}
                            </Link>
                            <Link
                                to={'/teams'}
                                className="inline-flex items-center gap-2 px-6 py-3 bg-card text-foreground border border-border font-semibold rounded-full shadow-lg hover:bg-muted transition-colors"
                            >
                                <Shield className="text-primary"/>
                                {t('buttons.viewTeams')}
                            </Link>
                        </>
                    )}
                    </div>

                    <div className="mt-20 grid grid-cols-1 md:grid-cols-2 gap-6 w-full max-w-225">
                    {volleyballFeatures.map((feature, i) => (
                        <div key={i} className="flex gap-4 p-5 bg-card border border-border rounded-2xl shadow-sm text-left">
                        <div className="shrink-0 size-12 flex items-center justify-center bg-accent rounded-xl text-primary">
                            {feature.icon}
                        </div>
                        <div>
                            <h3 className="font-semibold text-foreground">{t(feature.title)}</h3>
                            <p className="text-sm text-muted-foreground">{t(feature.description)}</p>
                        </div>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    )
}


const volleyballFeatures = [
  { icon: <Users/>, title: "features.users.title", description: "features.users.description" },
  { icon: <TrendingUp/>, title: "features.trendingUp.title", description: "features.trendingUp.description" },
  { icon: <Calendar/>, title: "features.calendar.title", description: "features.calendar.description" },
];