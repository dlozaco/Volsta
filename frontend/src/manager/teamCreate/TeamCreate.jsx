import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { Card, CardContent, CardHeader, CardTitle } from '#components/ui/card'
import { Button } from '#components/ui/button'
import { Input } from '#components/ui/input'
import { Label } from '#components/ui/label'
import { ShieldPlus } from 'lucide-react'
import { createTeam } from '@/services/team/teamApi'

export default function TeamCreate() {
    const navigate = useNavigate()
    const [form, setForm] = useState({ name: '', foundationDate: '', logoUrl: '' })
    const [saving, setSaving] = useState(false)
    const [error, setError] = useState(null)

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
                <h1 className="text-2xl font-bold">Create your team</h1>
                <p className="text-sm text-muted-foreground">
                    You will be assigned as the owner of this team automatically.
                </p>
            </div>

            {error && <div className="rounded-lg border border-red-200 bg-red-50 p-3 text-sm text-red-700">{error}</div>}

            <Card>
                <CardHeader><CardTitle className="flex items-center gap-2 text-base"><ShieldPlus className="size-4 text-primary" /> Team details</CardTitle></CardHeader>
                <CardContent>
                    <form onSubmit={submit} className="grid gap-4">
                        <div className="grid gap-2">
                            <Label htmlFor="teamName">Team name</Label>
                            <Input id="teamName" required maxLength={256} value={form.name} onChange={e => setForm({ ...form, name: e.target.value })} />
                        </div>
                        <div className="grid gap-2">
                            <Label htmlFor="foundationDate">Foundation date</Label>
                            <Input id="foundationDate" type="date" required value={form.foundationDate} onChange={e => setForm({ ...form, foundationDate: e.target.value })} />
                        </div>
                        <div className="grid gap-2">
                            <Label htmlFor="logoUrl">Logo URL</Label>
                            <Input id="logoUrl" type="url" placeholder="https://..." value={form.logoUrl} onChange={e => setForm({ ...form, logoUrl: e.target.value })} />
                        </div>
                        <div className="flex gap-2">
                            <Button type="submit" disabled={saving}>{saving ? 'Creating...' : 'Create team'}</Button>
                            <Button type="button" variant="outline" asChild><Link to="/teams">Cancel</Link></Button>
                        </div>
                    </form>
                </CardContent>
            </Card>
        </div>
    )
}
