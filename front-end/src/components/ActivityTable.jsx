import React, { useState } from 'react';
import {
    Table, TableBody, TableCell, TableHead, TableRow,
    IconButton, Tooltip, Button, Box, DialogContent, DialogTitle, Dialog, DialogActions
} from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import axios from "axios";

const ActivityTable = ({ activities, onEdit, onDelete, onAddTimeslot }) => {
    const [openTimeslotModal, setOpenTimeslotModal] = useState(false);
    const [selectedActivity, setSelectedActivity] = useState(null);
    const [timeslots, setTimeslots] = useState([]);

    const fetchTimeslots = async (activityId) => {
        try {
            const token = localStorage.getItem('token');
            const response = await axios.get(`/api/timeslots/activity/${activityId}`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            setTimeslots(response.data);
        } catch (err) {
            console.error('Klaida gaunant timeslots:', err);
        }
    };

    const openTimeslotForm = (activityId) => {
        setSelectedActivity(activityId);
        fetchTimeslots(activityId);  // Fetch timeslots for the selected activity
        setOpenTimeslotModal(true);   // Open the modal form
    };

    const closeTimeslotForm = () => {
        setOpenTimeslotModal(false);
        setTimeslots([]);  // Clear timeslots when closing
    };



    return (
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
                                <IconButton size="small" onClick={() => onEdit?.(a)}>
                                    <EditIcon fontSize="small" />
                                </IconButton>
                            </Tooltip>
                            <Tooltip title="Ištrinti">
                                <IconButton size="small" color="error" onClick={() => onDelete?.(a.id)}>
                                    <DeleteIcon fontSize="small" />
                                </IconButton>
                            </Tooltip>
                            <Button
                                onClick={() => onAddTimeslot(a)}
                                variant="contained"
                                color="primary"
                                sx={{ ml: 2 }}>
                                Pridėti laiką
                            </Button>
                            <Button
                                onClick={() => openTimeslotForm(a.id)}
                                variant="outlined"
                                color="secondary"
                                sx={{ ml: 2 }}
                            >
                                Peržiūrėti laikus
                            </Button>
                        </TableCell>
                    </TableRow>
            ))}
            </TableBody>
            <Dialog open={openTimeslotModal} onClose={closeTimeslotForm} fullWidth>
                <DialogTitle>Laikų sąrašas</DialogTitle>
                <DialogContent>
                    {timeslots.length === 0 ? (
                        <Box>Laikų nėra.</Box>
                    ) : (
                        <Box>
                            <Table>
                                <TableHead>
                                    <TableRow>
                                        <TableCell>Pradžia</TableCell>
                                        <TableCell>Pabaiga</TableCell>
                                        <TableCell>Maks. dalyviai</TableCell>
                                    </TableRow>
                                </TableHead>
                                <TableBody>
                                    {timeslots.map((slot) => (
                                        <TableRow key={slot.id}>
                                            <TableCell>{slot.startDateTime}</TableCell>
                                            <TableCell>{slot.endDateTime}</TableCell>
                                            <TableCell>{slot.maxParticipants}</TableCell>
                                        </TableRow>
                                    ))}
                                </TableBody>
                            </Table>
                        </Box>
                    )}
                </DialogContent>
                <DialogActions>
                    <Button onClick={closeTimeslotForm} color="secondary">
                        Uždaryti
                    </Button>
                </DialogActions>
            </Dialog>
        </Table>
    );
};

export default ActivityTable;