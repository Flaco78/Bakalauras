import React, { useEffect, useState } from 'react';
import {
    Box,
    Typography,
    Button,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Paper
} from '@mui/material';
import axios from 'axios';

const ProviderBookingsTab = () => {
    const [bookings, setBookings] = useState([]);

    const fetchBookings = async () => {
        try {
            const token = localStorage.getItem('token');
            const response = await axios.get('/api/bookings/provider', {
                headers: { Authorization: `Bearer ${token}` },
            });
            setBookings(response.data);
        } catch (err) {
            console.error('Klaida gaunant užsakymus:', err);
        }
    };

    const cancelBooking = async (bookingId) => {
        try {
            const token = localStorage.getItem('token');
            await axios.patch(`/api/bookings/${bookingId}/cancel`, {}, {
                headers: { Authorization: `Bearer ${token}` }
            });
            fetchBookings();
        } catch (err) {
            console.error('Klaida atšaukiant užsakymą:', err);
        }
    };

    useEffect(() => {
        fetchBookings();
    }, []);

    return (
        <Box>
            <Typography variant="h5" gutterBottom>
                Užsakymai mano veikloms
            </Typography>

            <Typography variant="h6" sx={{ mt: 3 }}>
                📌 Aktyvūs užsakymai
            </Typography>

            {bookings.filter(b => b.status !== 'CANCELLED').length === 0 ? (
                <Typography>Nėra aktyvių užsakymų.</Typography>
            ) : (
                <TableContainer component={Paper} sx={{ mt: 1 }}>
                    <Table>
                        <TableHead>
                            <TableRow sx={{ backgroundColor: '#ff9800' }}>
                                <TableCell sx={{ fontWeight: 'bold', color: '#fff' }}>Veikla</TableCell>
                                <TableCell sx={{ fontWeight: 'bold', color: '#fff' }}>Vaiko vardas</TableCell>
                                <TableCell sx={{ fontWeight: 'bold', color: '#fff' }}>Kontaktai</TableCell>
                                <TableCell sx={{ fontWeight: 'bold', color: '#fff' }}>Pradžia</TableCell>
                                <TableCell sx={{ fontWeight: 'bold', color: '#fff' }}>Pabaiga</TableCell>
                                <TableCell sx={{ fontWeight: 'bold', color: '#fff' }}>Statusas</TableCell>
                                <TableCell sx={{ fontWeight: 'bold', color: '#fff' }}>Veiksmai</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {bookings.filter(b => b.status !== 'CANCELLED').map((b) => (
                                <TableRow key={b.id}>
                                    <TableCell>{b.activityTitle}</TableCell>
                                    <TableCell>{b.childName}</TableCell>
                                    <TableCell>{b.email}</TableCell>
                                    <TableCell>{b.startDateTime}</TableCell>
                                    <TableCell>{b.endDateTime}</TableCell>
                                    <TableCell>
                                        <Typography
                                            variant="body2"
                                            color={
                                                b.status === 'APPROVED'
                                                    ? 'success.main'
                                                    : 'text.primary'
                                            }
                                        >
                                            {b.status}
                                        </Typography>
                                    </TableCell>
                                    <TableCell>
                                        {b.status === 'ACTIVE' && (
                                                <Button
                                                    variant="outlined"
                                                    color="error"
                                                    size="small"
                                                    onClick={() => cancelBooking(b.id)}
                                                    sx={{ mr: 1 }}
                                                >
                                                    Atšaukti
                                                </Button>
                                        )}
                                    </TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </TableContainer>
            )}

            {/* Cancelled Bookings */}
            <Typography variant="h6" sx={{ mt: 5 }}>
                ❌ Atšaukti užsakymai
            </Typography>

            {bookings.filter(b => b.status === 'CANCELLED').length === 0 ? (
                <Typography>Nėra atšauktų užsakymų.</Typography>
            ) : (
                <TableContainer component={Paper} sx={{ mt: 1 }}>
                    <Table>
                        <TableHead>
                            <TableRow sx={{ backgroundColor: '#bdbdbd' }}>
                                <TableCell sx={{ fontWeight: 'bold' }}>Veikla</TableCell>
                                <TableCell sx={{ fontWeight: 'bold' }}>Vaiko vardas</TableCell>
                                <TableCell sx={{ fontWeight: 'bold' }}>Kontaktai</TableCell>
                                <TableCell sx={{ fontWeight: 'bold' }}>Pradžia</TableCell>
                                <TableCell sx={{ fontWeight: 'bold' }}>Pabaiga</TableCell>
                                <TableCell sx={{ fontWeight: 'bold' }}>Statusas</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {bookings.filter(b => b.status === 'CANCELLED').map((b) => (
                                <TableRow key={b.id}>
                                    <TableCell>{b.activityTitle}</TableCell>
                                    <TableCell>{b.childName}</TableCell>
                                    <TableCell>{b.email}</TableCell>
                                    <TableCell>{b.startDateTime}</TableCell>
                                    <TableCell>{b.endDateTime}</TableCell>
                                    <TableCell>
                                        <Typography variant="body2" color="error">
                                            {b.status}
                                        </Typography>
                                    </TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </TableContainer>
            )}
        </Box>
    );
};

export default ProviderBookingsTab;