import React, { useEffect, useState } from 'react';
import axios from 'axios';
import {Box, Button, Divider, Typography} from '@mui/material';
import ActivityCard from '../components/ActivityCard.jsx';
import { useChild } from '../context/ChildContext.jsx';

const FavoriteActivities = () => {
    const { selectedChildId } = useChild();
    const [favoriteActivities, setFavoriteActivities] = useState([]);
    const [bookings, setBookings] = useState([]);
    const token = localStorage.getItem('token');

    useEffect(() => {
        if (!selectedChildId) return;

        const fetchData = async () => {
            try {
                const [favRes, bookingRes] = await Promise.all([
                    axios.get(`/api/interactions/favorites?childId=${selectedChildId}`, {
                        headers: { Authorization: `Bearer ${token}` }
                    }),
                    axios.get(`/api/bookings/child/${selectedChildId}`, {
                        headers: { Authorization: `Bearer ${token}` }
                    })
                ]);
                setFavoriteActivities(favRes.data);

                const bookings = bookingRes.data;
                if (bookings.length === 0) return [];
                const activityIds = bookings.map(b => b.activityId);

                const query = activityIds.map(id => `ids=${id}`).join('&');
                const activitiesResponse = await axios.get(`/api/activities/by-ids?${query}`, {
                    headers: { Authorization: `Bearer ${token}` }
                });

                const activitiesMap = new Map(
                    activitiesResponse.data.map(act => [act.id, act])
                );

                const enrichedBookings = bookings
                    .filter(b => b.status === 'ACTIVE')
                    .map(b => ({
                        ...activitiesMap.get(b.activityId),
                        bookingId: b.id,
                        startDateTime: b.startDateTime,
                        endDateTime: b.endDateTime,
                        childName: b.childName,
                        status: b.status,
                        providerEmail: b.providerEmail,
                    }));
                console.log('bookings:', bookings);

                setBookings(enrichedBookings);
            } catch (err) {
                console.error("❌ Failed to fetch data:", err);
            }
        };

        fetchData();
    }, [selectedChildId]);

    const handleCancelBooking = async (bookingId) => {
        try {
            await axios.patch(`/api/bookings/${bookingId}/cancel`, {}, {
                headers: { Authorization: `Bearer ${token}` }
            });

            // Re-fetch bookings after cancellation
            const bookingRes = await axios.get(`/api/bookings/child/${selectedChildId}`, {
                headers: { Authorization: `Bearer ${token}` }
            });

            const bookings = bookingRes.data.filter(b => b.status === 'ACTIVE');
            const activityIds = bookings.map(b => b.activityId);

            const query = activityIds.map(id => `ids=${id}`).join('&');
            const activitiesResponse = await axios.get(`/api/activities/by-ids?${query}`, {
                headers: { Authorization: `Bearer ${token}` }
            });

            const activitiesMap = new Map(
                activitiesResponse.data.map(act => [act.id, act])
            );

            const enrichedBookings = bookings.map(b => ({
                ...activitiesMap.get(b.activityId),
                bookingId: b.id
            }));

            setBookings(enrichedBookings);
        } catch (err) {
            console.error("❌ Failed to cancel booking:", err);
        }
    };
    return (
        <Box sx={{ padding: '40px', paddingTop: '120px' }}>
            <Typography variant="h5" sx={{ fontWeight: 'bold', mb: 3 }}>
                Mėgstamos veiklos {favoriteActivities.length > 0 && '❤️'}
            </Typography>

            {favoriteActivities.length > 0 ? (
                <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 3 }}>
                    {favoriteActivities.map(activity => (
                        <ActivityCard key={activity.id} activity={activity} />
                    ))}
                </Box>
            ) : (
                <Typography sx={{ color: '#777' }}>
                    Šiuo metu mėgstamų veiklų nėra.
                </Typography>
            )}
            <Divider sx={{ my: 3 }} />

            <Typography variant="h5" sx={{ fontWeight: 'bold', mb: 2 }}>
                Užsakytos veiklos {bookings.length > 0 && '📅'}
            </Typography>

            {bookings.length > 0 ? (
                <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 3 }}>
                    {bookings.map(b => (
                        <Box key={b.id} sx={{ position: 'relative', width: 300 }}>
                            <Box
                                sx={{
                                    overflow: 'hidden',
                                    display: 'flex',
                                    flexDirection: 'column',
                                    justifyContent: 'space-between',
                                    p: 3,
                                    backgroundColor: '#f9f9f9',
                                    border: '2px solid #422800',
                                    borderRadius: '30px',
                                    position: 'relative',
                                }}
                            >
                                <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
                                    🗓️ {new Date(b.startDateTime).toLocaleDateString('lt-LT')}
                                    /
                                    {new Date(b.endDateTime).toLocaleDateString('lt-LT')}
                                    <br />
                                    🕓 {new Date(b.startDateTime).toLocaleTimeString('lt-LT', {
                                    hour: '2-digit',
                                    minute: '2-digit'
                                })} - {new Date(b.endDateTime).toLocaleTimeString('lt-LT', {
                                    hour: '2-digit',
                                    minute: '2-digit'
                                })}
                                </Box>
                                <Button
                                    onClick={() => handleCancelBooking(b.bookingId)}
                                    variant="outlined"
                                    color="error"
                                    size="small"
                                >
                                    Atšaukti
                                </Button>
                            </Box>
                            <ActivityCard activity={b} />
                        </Box>
                    ))}
                </Box>
            ) : (
                <Typography sx={{ color: '#777' }}>
                    Šiuo metu nėra užsakytų veiklų.
                </Typography>
            )}
        </Box>
    );
};

export default FavoriteActivities;