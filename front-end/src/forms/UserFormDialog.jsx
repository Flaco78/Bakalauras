import React, { useEffect, useState } from 'react';
import {
    Dialog, DialogTitle, DialogContent, DialogActions,
    TextField, Button, MenuItem,
} from '@mui/material';
import axios from 'axios';
import { roleOptions } from '../utils/constants';


const UserFormDialog = ({ open, onClose, user, refreshUsers }) => {
    const isEditMode = Boolean(user);

    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [address, setAddress] = useState('');
    const [role, setRole] = useState('');

    useEffect(() => {
        if (isEditMode) {
            setEmail(user.email || '');
            setAddress(user.address || '');
            setRole(user.roles?.[0] || '');
        } else {
            setEmail('');
            setPassword('');
            setAddress('');
            setRole('');
        }
    }, [user]);


// Pakeistas kodas, kad `roles` siųstų ID, o ne pavadinimus
    const handleSave = async () => {
        const token = localStorage.getItem('token');
        const payload = {
            email,
            password: isEditMode ? undefined : password, // Only send password when creating
            address,
            roles: [
                {
                    id: roleOptions.find(option => option.name === role)?.id,
                    name: role
                }
            ]
        };

        try {
            if (isEditMode) {
                await axios.put(`/api/users/${user.id}`, payload, {
                    headers: { Authorization: `Bearer ${token}` }
                });
            } else {
                await axios.post('/api/users/create', payload, {
                    headers: { Authorization: `Bearer ${token}` }
                });
            }

            refreshUsers();
            onClose();
        } catch (e) {
            console.error('Failed to save user:', e);
        }
    };

    return (
        <Dialog open={open} onClose={onClose} fullWidth>
            <DialogTitle>{isEditMode ? 'Redaguoti vartotoją' : 'Naujas vartotojas'}</DialogTitle>
            <DialogContent>
                <TextField
                    fullWidth label="El. paštas" margin="normal"
                    value={email} onChange={(e) => setEmail(e.target.value)}
                />
                {!isEditMode && (
                    <TextField
                        fullWidth label="Slaptažodis" type="password" margin="normal"
                        value={password} onChange={(e) => setPassword(e.target.value)}
                    />
                )}
                <TextField
                    fullWidth label="Adresas" margin="normal"
                    value={address} onChange={(e) => setAddress(e.target.value)}
                />
                <TextField
                    fullWidth
                    select
                    label="Rolė"
                    margin="normal"
                    value={role}
                    onChange={(e) => setRole(e.target.value)}
                >
                    {roleOptions.map((r) => (
                        <MenuItem key={r.id} value={r.name}>
                            {r.name}
                        </MenuItem>
                    ))}
                </TextField>
            </DialogContent>
            <DialogActions>
                <Button onClick={onClose}>Atšaukti</Button>
                <Button variant="contained" onClick={handleSave}>
                    {isEditMode ? 'Išsaugoti' : 'Sukurti'}
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default UserFormDialog;