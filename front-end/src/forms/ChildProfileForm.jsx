import React, { useState, useEffect } from 'react';
import {TextField, MenuItem, DialogActions, Typography} from '@mui/material';
import axios from 'axios';
import ButtonFunky from "../components/ButtonFunky.jsx";

const ChildProfileForm = ({ child, onClose, isEdit, setChildrenProfiles }) => {
    const [name, setName] = useState(child?.name || '');
    const [birthDate, setBirthDate] = useState(child?.birthDate || '');
    const [gender, setGender] = useState(child?.gender || '');

    useEffect(() => {
        if (isEdit && child) {
            setName(child.name);
            setBirthDate(child.birthDate);
            setGender(child.gender);
        }
    }, [child, isEdit]);

    const handleSubmit = async () => {
        const data = { name, birthDate, gender };

        const token = localStorage.getItem('token');
        const config = {
            headers: {
                Authorization: `Bearer ${token}`
            }
        };

        try {
            if (isEdit) {
                await axios.put(`/api/child-profiles/${child.id}`, data, config);
            } else {
                await axios.post('/api/child-profiles/create', data, config);
            }
            onClose();
            const response = await axios.get('api/auth/user/child-profiles', {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem('token')}`
                }
            });
            setChildrenProfiles(response.data);
        } catch (error) {
            console.error('Klaida išsaugant:', error);
        }
    };
    return (
        <form>
            <Typography variant="h5" sx={{textAlign: 'left', mb: 1, mt: 2}}>Name</Typography>
            <TextField
                fullWidth
                value={name}
                onChange={(e) => setName(e.target.value)}
            />
            <Typography variant="h5" sx={{textAlign: 'left', mb: 1, mt: 2}}>Birth date</Typography>
            <TextField
                type="date"
                fullWidth
                value={birthDate}
                onChange={(e) => setBirthDate(e.target.value)}
            />
            <Typography variant="h5" sx={{textAlign: 'left', mb: 1, mt: 2}}>Gender</Typography>
            <TextField
                select
                fullWidth
                value={gender}
                onChange={(e) => setGender(e.target.value)}
            >
                <MenuItem value="MALE">Male</MenuItem>
                <MenuItem value="FEMALE">Female</MenuItem>
                <MenuItem value="OTHER">Other</MenuItem>
            </TextField>
            <DialogActions>
                <ButtonFunky onClick={onClose}>Cancel</ButtonFunky>
                <ButtonFunky onClick={handleSubmit}>{isEdit ? 'Save' : 'Create'}</ButtonFunky>
            </DialogActions>
        </form>
    );
};

export default ChildProfileForm;