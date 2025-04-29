import React, { useState, useEffect } from 'react';
import {TextField, MenuItem, DialogActions, Typography,} from '@mui/material';
import axios from 'axios';
import ButtonFunky from "../components/ButtonFunky.jsx";
import InterestSelector from "../components/IntrestSelector.jsx";

const ChildProfileForm = ({ child, onClose, isEdit, setChildrenProfiles }) => {
    const [name, setName] = useState(child?.name || '');
    const [birthDate, setBirthDate] = useState(child?.birthDate || '');
    const [gender, setGender] = useState(child?.gender || '');
    const [maxActivityDuration, setMaxActivityDuration] = useState(child?.maxActivityDuration || '');
    const [preferredDeliveryMethod, setPreferredDeliveryMethod] = useState(child?.preferredDeliveryMethod || '');
    const [interests, setInterests] = useState([]);

    useEffect(() => {
        if (isEdit && child) {
            setName(child.name);
            setBirthDate(child.birthDate);
            setGender(child.gender);
            setMaxActivityDuration(child.maxActivityDuration || '');
            setPreferredDeliveryMethod(child.preferredDeliveryMethod || '');
            setInterests(child.interests || []);
        }
    }, [child, isEdit]);

    const handleSubmit = async () => {
        const data = {
            name,
            birthDate,
            gender,
            maxActivityDuration,
            preferredDeliveryMethod,
        };

        const token = localStorage.getItem('token');
        const config = {
            headers: {
                Authorization: `Bearer ${token}`
            }
        };

        try {
            let childId;

            if (isEdit) {
                await axios.put(`/api/child-profiles/${child.id}`, data, config);
                childId = child.id;
            } else {
                const response = await axios.post('/api/child-profiles/create', data, config);
                childId = response.data.id;
            }

            if (childId && interests.length > 0) {
                await axios.put(`/api/child-profiles/children/${childId}/interests`, interests, config);
            }

            onClose();

            const response = await axios.get('api/auth/user/child-profiles', config);
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

            <Typography variant="h5" sx={{textAlign: 'left', mb: 1, mt: 2}}>Max activity duration (minutes)</Typography>
            <TextField

                type="number"
                value={maxActivityDuration}
                onChange={(e) => setMaxActivityDuration(e.target.value)}
                fullWidth
                required
            />

            <Typography variant="h5" sx={{textAlign: 'left', mb: 1, mt: 2}}>Preferred delivery method</Typography>
                <TextField
                    select
                    fullWidth
                    value={preferredDeliveryMethod}
                    onChange={(e) => setPreferredDeliveryMethod(e.target.value)}
                >
                    <MenuItem value="ONLINE">Online</MenuItem>
                    <MenuItem value="ONSITE">Onsite</MenuItem>
                </TextField>

            <InterestSelector selected={interests} setSelected={setInterests} />


            <DialogActions>
                <ButtonFunky onClick={onClose}>Cancel</ButtonFunky>
                <ButtonFunky onClick={handleSubmit}>{isEdit ? 'Save' : 'Create'}</ButtonFunky>
            </DialogActions>
        </form>
    );
};

export default ChildProfileForm;