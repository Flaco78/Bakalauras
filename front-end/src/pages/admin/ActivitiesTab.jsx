import React, { useEffect, useState } from 'react';
import {
    Box, Table, TableBody, TableCell, TableHead, TableRow,
    Typography, IconButton, Tooltip, Button
} from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import EditIcon from '@mui/icons-material/Edit';
import ActivityFormDialog from '../../forms/ActivityFormDialog';
import axios from 'axios';

const ActivitiesTab = () => {
    const [activities, setActivities] = useState([]);
    const [dialogOpen, setDialogOpen] = useState(false);
    const [selectedActivity, setSelectedActivity] = useState(null);

    const handleOpenCreate = () => {
        setSelectedActivity(null);
        setDialogOpen(true);
    };

    const handleEdit = (activity) => {
        setSelectedActivity(activity);
        setDialogOpen(true);
    };

    const handleSave = (savedActivity) => {
        setActivities((prev) => {
            const exists = prev.find((a) => a.id === savedActivity.id);
            if (exists) {
                return prev.map((a) => (a.id === savedActivity.id ? savedActivity : a));
            } else {
                return [...prev, savedActivity];
            }
        });
    };

    useEffect(() => {
        axios.get('/api/activities/every', {
            headers: {
                Authorization: `Bearer ${localStorage.getItem('token')}`,
            },
        })
            .then((res) => setActivities(res.data))
            .catch((err) => console.error('Nepavyko gauti veiklų:', err));
    }, []);

    const handleDelete = async (id) => {
        if (!window.confirm('Ar tikrai nori ištrinti šią veiklą?')) return;
        try {
            await axios.delete(`/api/activities/${id}`, {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem('token')}`,
                },
            });
            setActivities(prev => prev.filter(a => a.id !== id));
        } catch (error) {
            console.error('Klaida trinant veiklą:', error);
            alert('Įvyko klaida trinant veiklą.');
        }
    };



    return (
        <Box>
            <Typography variant="h6" sx={{ mb: 2 }}>Visos veiklos</Typography>
            <Button variant="contained" onClick={handleOpenCreate} sx={{ mb: 2 }}>
                ➕ Nauja veikla
            </Button>
            <Table>
                <TableHead>
                    <TableRow>
                        <TableCell>Pavadinimas</TableCell>
                        <TableCell>Kategorija</TableCell>
                        <TableCell>Kaina</TableCell>
                        <TableCell>Lokacija</TableCell>
                        <TableCell>Teikėjas</TableCell>
                        <TableCell>Veiksmai</TableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                    {activities.map((a) => (
                        <TableRow key={a.id}>
                            <TableCell>{a.title}</TableCell>
                            <TableCell>{a.category}</TableCell>
                            <TableCell>{a.price} € / {a.priceType}</TableCell>
                            <TableCell>{a.location || 'Online'}</TableCell>
                            {a.providerType === 'INDIVIDUAL' && (
                            <TableCell>{a.providerName}</TableCell>
                                )}
                            {a.providerType === 'COMPANY' && (
                                <TableCell>{a.companyName}</TableCell>
                            )}
                            <TableCell>
                                <Tooltip title="Redaguoti">
                                    <IconButton size="small" onClick={() => handleEdit(a)}>
                                        <EditIcon fontSize="small" />
                                    </IconButton>
                                </Tooltip>
                                <Tooltip title="Ištrinti">
                                    <IconButton size="small" color="error" onClick={() => handleDelete(a.id)}>
                                        <DeleteIcon fontSize="small" />
                                    </IconButton>
                                </Tooltip>
                            </TableCell>
                        </TableRow>
                    ))}
                    <ActivityFormDialog
                        open={dialogOpen}
                        onClose={() => setDialogOpen(false)}
                        activity={selectedActivity}
                        onSave={handleSave}
                    />
                </TableBody>
            </Table>
        </Box>
    );
};

export default ActivitiesTab;