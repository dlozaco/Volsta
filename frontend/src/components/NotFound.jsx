import { FileQuestion, Home } from "lucide-react"
import { Link } from "react-router-dom"
import { useTranslation } from "react-i18next"

export default function NotFound() {
    const { t } = useTranslation('notFound')

    return (
        <div className="relative flex flex-col items-center justify-center min-h-[calc(100vh-64px)] text-center p-6 bg-background">
            <div className="absolute inset-0 bg-linear-to-br from-background via-slate-950/20 to-background opacity-70 z-0" />

            <div className="relative z-10 flex flex-col items-center">
                <FileQuestion className="w-24 h-24 md:w-32 md:h-32 text-primary mb-4" />

                <h1 className="text-8xl font-extrabold tracking-tighter text-foreground md:text-9xl">
                    {t('title')}
                </h1>

                <h2 className="mt-5 text-2xl font-semibold tracking-tight text-foreground md:text-3xl">
                    {t('heading')}
                </h2>

                <p className="mt-3 text-lg text-muted-foreground max-w-md">
                    {t('description')}
                </p>

                <Link
                    to={'/'}
                    className="mt-10 inline-flex items-center gap-2 px-6 py-3 bg-primary text-primary-foreground font-semibold rounded-full shadow-lg hover:bg-primary/90 transition-colors">
                    <Home />
                    {t('button')}
                </Link>
            </div>
        </div>
    )
}