import { useEffect, useState } from 'react'
import { useAuth } from '../auth/AuthContext'
import { getManagerProfile, updateManagerProfile, changePassword } from '../services/api'
import { Card, CardContent, CardHeader, CardTitle } from '#components/ui/card'
import { Button } from '#components/ui/button'
import { Input } from '#components/ui/input'
import { Label } from '#components/ui/label'
import { Pencil, Save, X, LogOut, KeyRound } from 'lucide-react'
import { Link } from 'react-router-dom'

export default function ManagerProfile() {
    const { user, logout } = useAuth()
    const [profile, setProfile] = useState(null)
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState(null)
    const [editing, setEditing] = useState(false)
    const [saving, setSaving] = useState(false)
    const [form, setForm] = useState({ name: '', surname: '', email: '', phoneNumber: '', photoUrl: '' })

    const [passwordOpen, setPasswordOpen] = useState(false)
    const [passwordForm, setPasswordForm] = useState({ currentPassword: '', newPassword: '', repeatPassword: '' })
    const [passwordMsg, setPasswordMsg] = useState(null)
    const [passwordSaving, setPasswordSaving] = useState(false)

    useEffect(() => {
        getManagerProfile()
            .then(data => {
                setProfile(data)
                setForm({ name: data.name, surname: data.surname, email: data.email, phoneNumber: data.phoneNumber, photoUrl: data.photoUrl || '' })
            })
            .catch(err => setError(err.message))
            .finally(() => setLoading(false))
    }, [])

    const handleChange = (field) => (e) => {
        setForm(prev => ({ ...prev, [field]: e.target.value }))
    }

    const handleSave = async () => {
        setSaving(true)
        setError(null)
        try {
            const updated = await updateManagerProfile(form)
            setProfile(updated)
            setEditing(false)
        } catch (err) {
            setError(err.message)
        } finally {
            setSaving(false)
        }
    }

    const handleCancel = () => {
        setForm({ name: profile.name, surname: profile.surname, email: profile.email, phoneNumber: profile.phoneNumber, photoUrl: profile.photoUrl || '' })
        setEditing(false)
        setError(null)
    }

    const handlePasswordChange = (field) => (e) => {
        setPasswordForm(prev => ({ ...prev, [field]: e.target.value }))
    }

    const handleChangePassword = async (e) => {
        e.preventDefault()
        setPasswordMsg(null)
        if (passwordForm.newPassword !== passwordForm.repeatPassword) {
            setPasswordMsg({ type: 'error', text: 'New passwords do not match' })
            return
        }
        setPasswordSaving(true)
        try {
            await changePassword(passwordForm.currentPassword, passwordForm.newPassword)
            setPasswordMsg({ type: 'success', text: 'Password updated successfully' })
            setPasswordForm({ currentPassword: '', newPassword: '', repeatPassword: '' })
            setPasswordOpen(false)
        } catch (err) {
            setPasswordMsg({ type: 'error', text: err.message })
        } finally {
            setPasswordSaving(false)
        }
    }

    if (loading) return <div className="flex items-center justify-center min-h-screen">Loading profile...</div>

    const initials = (profile?.name?.charAt(0) || '') + (profile?.surname?.charAt(0) || '')

    return (
        <div className="mx-auto max-w-2xl space-y-6">
            <div className="flex items-center justify-between">
                <h1 className="text-2xl font-bold">My Profile</h1>
                <div className="flex gap-2">
                    {editing ? (
                        <>
                            <Button variant="outline" size="sm" onClick={handleCancel} disabled={saving}>
                                <X className="mr-1 size-4" /> Cancel
                            </Button>
                            <Button size="sm" onClick={handleSave} disabled={saving}>
                                <Save className="mr-1 size-4" /> {saving ? 'Saving...' : 'Save'}
                            </Button>
                        </>
                    ) : (
                        <>
                            <Button variant="outline" size="sm" onClick={() => setPasswordOpen(!passwordOpen)}>
                                <KeyRound className="mr-1 size-4" /> Change password
                            </Button>
                            <Button variant="outline" size="sm" onClick={() => setEditing(true)}>
                                <Pencil className="mr-1 size-4" /> Edit
                            </Button>
                            <Button variant="ghost" size="sm" onClick={logout}>
                                <LogOut className="mr-1 size-4" /> Logout
                            </Button>
                        </>
                    )}
                </div>
            </div>

            {error && (
                <div className="rounded-lg border border-red-200 bg-red-50 p-3 text-sm text-red-700">
                    {error}
                </div>
            )}

            {passwordOpen && (
                <Card>
                    <CardHeader>
                        <CardTitle className="text-base">Change password</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <form onSubmit={handleChangePassword} className="space-y-4">
                            <div className="grid gap-2">
                                <Label htmlFor="currentPassword">Current password</Label>
                                <Input id="currentPassword" type="password" required value={passwordForm.currentPassword} onChange={handlePasswordChange('currentPassword')} />
                            </div>
                            <div className="grid gap-2">
                                <Label htmlFor="newPassword">New password</Label>
                                <Input id="newPassword" type="password" required value={passwordForm.newPassword} onChange={handlePasswordChange('newPassword')} />
                                <p className="text-xs text-muted-foreground">8+ characters, one lowercase, one uppercase and one special character (@#$%^&+=!).</p>
                            </div>
                            <div className="grid gap-2">
                                <Label htmlFor="repeatPassword">Repeat new password</Label>
                                <Input id="repeatPassword" type="password" required value={passwordForm.repeatPassword} onChange={handlePasswordChange('repeatPassword')} />
                            </div>
                            {passwordMsg && (
                                <p className={`text-sm ${passwordMsg.type === 'success' ? 'text-green-600' : 'text-red-600'}`}>
                                    {passwordMsg.text}
                                </p>
                            )}
                            <Button type="submit" disabled={passwordSaving}>
                                {passwordSaving ? 'Updating...' : 'Update password'}
                            </Button>
                        </form>
                    </CardContent>
                </Card>
            )}

            <div className="flex flex-col gap-6 lg:flex-row">
                <div className="w-full lg:w-64">
                    <Card>
                        <CardContent className="flex flex-col items-center gap-4 py-8">
                            {profile?.photoUrl ? (
                                <img src={profile.photoUrl} alt="Profile" className="h-28 w-28 rounded-full object-cover" />
                            ) : (
                                <div className="flex h-28 w-28 items-center justify-center rounded-full bg-primary text-4xl font-bold text-primary-foreground">
                                    {initials || '?'}
                                </div>
                            )}
                            <div className="text-center">
                                <h2 className="text-lg font-semibold">{profile?.name} {profile?.surname}</h2>
                                <p className="text-sm text-muted-foreground">{user?.username}</p>
                            </div>
                        </CardContent>
                    </Card>
                </div>

                <div className="flex-1">
                    <Card>
                        <CardHeader>
                            <CardTitle>Personal Information</CardTitle>
                        </CardHeader>
                        <CardContent className="space-y-4">
                            <div className="grid gap-2">
                                <Label htmlFor="name">Name</Label>
                                <Input id="name" value={form.name} onChange={handleChange('name')} disabled={!editing} />
                            </div>
                            <div className="grid gap-2">
                                <Label htmlFor="surname">Surname</Label>
                                <Input id="surname" value={form.surname} onChange={handleChange('surname')} disabled={!editing} />
                            </div>
                            <div className="grid gap-2">
                                <Label htmlFor="email">Email</Label>
                                <Input id="email" type="email" value={form.email} onChange={handleChange('email')} disabled={!editing} />
                            </div>
                            <div className="grid gap-2">
                                <Label htmlFor="phoneNumber">Phone</Label>
                                <Input id="phoneNumber" value={form.phoneNumber} onChange={handleChange('phoneNumber')} disabled={!editing} />
                            </div>
                            <div className="grid gap-2">
                                <Label htmlFor="photoUrl">Profile photo URL</Label>
                                <Input id="photoUrl" type="url" placeholder="https://..." value={form.photoUrl} onChange={handleChange('photoUrl')} disabled={!editing} />
                            </div>
                        </CardContent>
                    </Card>

                    {profile?.teams?.length > 0 && (
                        <Card className="mt-6">
                            <CardHeader>
                                <CardTitle>Teams</CardTitle>
                            </CardHeader>
                            <CardContent>
                                <ul className="space-y-1">
                                    {profile.teams.map((team, i) => (
                                        <Link key={i} to={`/teams/${team}`}>
                                            <li key={i} className="text-sm text-muted-foreground">{team}</li>
                                        </Link>
                                    ))}
                                </ul>
                            </CardContent>
                        </Card>
                    )}
                </div>
            </div>
        </div>
    )
}
