import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { Card, CardContent, CardHeader, CardTitle } from '#components/ui/card'
import { Button } from '#components/ui/button'
import { Input } from '#components/ui/input'
import { Label } from '#components/ui/label'
import { ShieldPlus } from 'lucide-react'
import { createTeam } from '@/services/team/teamApi'
import { useTranslation } from 'react-i18next'

export default function TeamCreate() {
    const navigate = useNavigate()
    const [form, setForm] = useState({ name: '', foundationDate: '', logoUrl: '' })
    const [saving, setSaving] = useState(false)
    const [error, setError] = useState(null)
    const { t } = useTranslation('teamCreate')

    const submit = async (e) => {
        e.preventDefault()
        setSaving(true)
        setError(null)
        try {
            const created = await createTeam({
                name: form.name,
                foundationDate: form.foundationDate,
                logoUrl: form.logoUrl || null,
            })
            navigate(`/teams/${encodeURIComponent(created.name)}`)
        } catch (err) {
            setError(err.message)
        } finally {
            setSaving(false)
        }
    }

    return (
        <div className="mx-auto max-w-2xl space-y-6">
            <div className="space-y-1">
                <h1 className="text-2xl font-bold">{t('header.title')}</h1>
                <p className="text-sm text-muted-foreground">
                    {t('header.subtitle')}
                </p>
            </div>

            {error && <div className="rounded-lg border border-red-200 bg-red-50 p-3 text-sm text-red-700">{error}</div>}

            <Card>
                <CardHeader><CardTitle className="flex items-center gap-2 text-base"><ShieldPlus className="size-4 text-primary" /> {t('form.sectionTitle')}</CardTitle></CardHeader>
                <CardContent>
                    <form onSubmit={submit} className="grid gap-4">
                        <div className="grid gap-2">
                            <Label htmlFor="teamName">{t('form.labels.name')}</Label>
                            <Input id="teamName" required maxLength={256} value={form.name} onChange={e => setForm({ ...form, name: e.target.value })} />
                        </div>
                        <div className="grid gap-2">
                            <Label htmlFor="foundationDate">{t('form.labels.foundationDate')}</Label>
                            <Input id="foundationDate" type="date" required value={form.foundationDate} onChange={e => setForm({ ...form, foundationDate: e.target.value })} />
                        </div>
                        <div className="grid gap-2">
                            <Label htmlFor="logoUrl">{t('form.labels.logoUrl')}</Label>
                            <Input id="logoUrl" type="url" placeholder="https://..." value={form.logoUrl} onChange={e => setForm({ ...form, logoUrl: e.target.value })} />
                        </div>
                        <div className="flex gap-2">
                            <Button type="submit" disabled={saving}>{saving ? 'Creating...' : t('form.buttons.submit')}</Button>
                            <Button type="button" variant="outline" asChild><Link to="/teams">{t('form.buttons.cancel')}</Link></Button>
                        </div>
                    </form>
                </CardContent>
            </Card>
        </div>
    )
}
