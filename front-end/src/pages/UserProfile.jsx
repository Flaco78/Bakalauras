import React, { useState, useEffect } from 'react';
import {
    Box,
    Typography,
    TextField,
    Divider,
    DialogContent,
    Dialog
} from '@mui/material';
import UserAvatar from '../components/UserAvatar';
import ChildProfileForm from '../forms/ChildProfileForm';
import ButtonFunky from '../components/ButtonFunky';
import axios from "axios";
import ChildProfileCard from "../forms/ChildProfileCard.jsx";
import {useAuth} from "../context/AuthContext.jsx";

const UserProfile = () => {
    const [user, setUser] = useState(null);
    const [address, setAddress] = useState('');
    const [email, setEmail] = useState('');
    const { roles } = useAuth();
    const [password, setPassword] = useState('');
    const [openCreateDialog, setOpenCreateDialog] = useState(false);
    const [openEditDialog, setOpenEditDialog] = useState(false);
    const [selectedChild, setSelectedChild] = useState(null);
    const [childrenProfiles, setChildrenProfiles] = useState([]);

    useEffect(() => {
        const token = localStorage.getItem('token');
        console.log("Roles:", roles);
        if (roles.includes('PROVIDER'))  {
            axios.get('/api/auth/provider', {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            })
                .then(res => {
                    setUser(res.data);
                    setEmail(res.data.email || '');
                })
                .catch(err => console.error("Klaida gaunant PROVIDER info:", err));
        } else {
            axios.get('/api/auth/user', {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            })
                .then(res => {
                    const data = res.data;
                    setUser(data);
                    setAddress(data.address || '');
                    setEmail(data.email || '');
                })
                .catch(err => console.error("Klaida gaunant USER info:", err));

            axios.get('/api/auth/user/child-profiles', {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            })
                .then(res => setChildrenProfiles(res.data))
                .catch(err => console.error("Klaida gaunant vaikus:", err));
        }
    }, []);

    const handleSave = () => {
        if (roles.includes('PROVIDER')) {
        axios.put('/api/auth/provider', { email, password }, {
            headers: {
                Authorization: `Bearer ${localStorage.getItem('token')}`
            }
        })
            .then(() => alert('Pakeitimai išsaugoti!'))
            .catch(err => alert('Klaida išsaugant'));
    } else {
        axios.put('/api/auth/user', {address, email, password}, {
            headers: {
                Authorization: `Bearer ${localStorage.getItem('token')}`
            }
        })
            .then(() => alert('Pakeitimai išsaugoti!'))
            .catch(err => alert('Klaida išsaugant'));
    }
    }

    const handleDeleteChildProfile = async (childId) => {
        if (!window.confirm('Ar tikrai nori ištrinti šį profilį?')) return;

        try {
            await axios.delete(`/api/child-profiles/${childId}`, {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem('token')}`
                }
            });
            const response = await axios.get('api/auth/user/child-profiles', {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem('token')}`
                }
            });
            setChildrenProfiles(response.data);
        } catch (error) {
            console.error('Klaida trinant:', error);
        }
    };

    const handleCreateChildProfile = () => {
        setOpenCreateDialog(true);
    };

    const handleEditChildProfile = (child) => {
        setSelectedChild(child);
        setOpenEditDialog(true);
    };

    const handleCloseDialog = () => {
        setOpenCreateDialog(false);
        setOpenEditDialog(false);
        setSelectedChild(null);
    };

    if (!user) return <div>Kraunama...</div>;

    return (
        <Box sx={{
            padding: 4,
            maxWidth: '600px',
            margin: 'auto',
            mt: 4
        }}>
            <Typography variant="h3" sx={{ fontFamily: '"Comic Sans MS", cursive, sans-serif', fontWeight: 'bold', color: 'rgba(0,0,0,0.87)' }}>
                Mano paskyra
            </Typography>
            <Box sx={{ width: '100%', display: 'flex', justifyContent: 'center', mb: 3 }}>
                <img src="/Underline_01.png" alt="underline" style={{ width: '70%' }} />
            </Box>

            <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 2 }}>
                <UserAvatar name={user.email} />
                <Typography variant="h6">{user.email}</Typography>
            </Box>

            <Divider sx={{ my: 3 }} />

            <Typography variant="h5" sx={{textAlign: 'left', mb: 1}}>El. paštas</Typography>
            <TextField
                required
                fullWidth
                value={email}
                onChange={(e) => setEmail(e.target.value)}
            />
            {(roles.includes('USER')) && (
                <>
            <Typography variant="h5" sx={{textAlign: 'left', mb: 1, mt: 2}}>Adresas</Typography>
            <TextField
                required
                fullWidth
                value={address}
                onChange={(e) => setAddress(e.target.value)}
            />
            </>
             )}
            <Typography variant="h5" sx={{textAlign: 'left', mb: 1, mt: 2}}>Naujas slaptažodis</Typography>
            <TextField
                type="password"
                fullWidth
                value={password}
                onChange={(e) => setPassword(e.target.value)}
            />
            <ButtonFunky fullWidth sx={{ mt: 2 }} onClick={handleSave}>Išsaugoti</ButtonFunky>

            {(roles.includes('USER')) && (
                <>
            <Divider sx={{ my: 4 }} />
            <Typography variant="h3" sx={{ fontFamily: '"Comic Sans MS", cursive, sans-serif', fontWeight: 'bold', color: 'rgba(0,0,0,0.87)' }}>
               Vaiko paskyra
            </Typography>
            <Box sx={{ width: '100%', display: 'flex', justifyContent: 'center', mb: 3 }}>
                <img src="/Underline_07.png" alt="underline" style={{ width: '70%' }} />
            </Box>


            <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 2 }}>
                {childrenProfiles.map(child => (
                    <ChildProfileCard
                        key={child.id}
                        child={child}
                        onEdit={handleEditChildProfile}
                        onDelete={handleDeleteChildProfile} />
                ))}
            </Box>

            <ButtonFunky sx={{ mt: 2 }} onClick={handleCreateChildProfile}>Sukurti vaiko profilį</ButtonFunky>
                </>
            )}

            <Dialog open={openEditDialog} onClose={handleCloseDialog}>
                <DialogContent sx={{ borderRadius: '30px' }}>
                    <ChildProfileForm
                        child={selectedChild}
                        onClose={handleCloseDialog}
                        isEdit={true}
                        setChildrenProfiles={setChildrenProfiles}
                    />
                </DialogContent>
            </Dialog>

            <Dialog open={openCreateDialog} onClose={handleCloseDialog}>
                <DialogContent sx={{ borderRadius: '30px' }}>
                    <ChildProfileForm
                        child={selectedChild}
                        onClose={handleCloseDialog}
                        isEdit={false}
                        setChildrenProfiles={setChildrenProfiles}
                    />
                </DialogContent>
            </Dialog>
        </Box>
    );
};

export default UserProfile;