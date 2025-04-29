import React, { useState } from 'react';
import { Dialog, DialogTitle, DialogContent, DialogActions, Button, TextField } from '@mui/material';
import axios from 'axios';

const TimeslotFormDialog = ({ open, onClose, activity, onSave }) => {
    const [formData, setFormData] = useState({
        startDateTime: '',
        endDateTime: '',
        maxParticipants: 0,
    });

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({
            ...prev,
            [name]: value,
        }));
    };

    const handleSubmit = async () => {
        try {
            const response = await axios.post(`/api/timeslots/activity/${activity.id}`, formData, {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem('token')}`,
                },
            });
            onSave(response.data);
            onClose();
        } catch (err) {
            console.error('Error saving timeslot:', err);
        }
    };

    return (
        <Dialog open={open} onClose={onClose} fullWidth>
            <DialogTitle>Pridėti laiką</DialogTitle>
            <DialogContent>
                <TextField
                    label="Pradžios laikas"
                    type="datetime-local"
                    name="startDateTime"
                    value={formData.startDateTime}
                    onChange={handleChange}
                    fullWidth
                    margin="dense"
                    InputLabelProps={{
                        shrink: true,
                    }}
                />
                <TextField
                    label="Pabaigos laikas"
                    type="datetime-local"
                    name="endDateTime"
                    value={formData.endDateTime}
                    onChange={handleChange}
                    fullWidth
                    margin="dense"
                    InputLabelProps={{
                        shrink: true,
                    }}
                />
                <TextField
                    label="Maksimalus dalyvių skaičius"
                    type="number"
                    name="maxParticipants"
                    value={formData.maxParticipants}
                    onChange={handleChange}
                    fullWidth
                    margin="dense"
                />
            </DialogContent>
            <DialogActions>
                <Button onClick={onClose}>Atšaukti</Button>
                <Button onClick={handleSubmit} variant="contained">Išsaugoti</Button>
            </DialogActions>
        </Dialog>
    );
};

export default TimeslotFormDialog;