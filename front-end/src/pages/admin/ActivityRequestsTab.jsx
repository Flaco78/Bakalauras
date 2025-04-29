import React, { useEffect, useState } from 'react';
import { Typography, Grid, Box } from '@mui/material';
import axios from 'axios';
import ActivityRequestCard from '../../forms/ActivityRequestCard';

const ActivityRequestsTab = () => {
    const [activities, setActivities] = useState([]);

    const fetchPendingActivities = async () => {
        try {
            const token = localStorage.getItem('token');
            const response = await axios.get('/api/activities/pending', {
                headers: { Authorization: `Bearer ${token}` }
            });
            setActivities(response.data);
        } catch (err) {
            console.error("Klaida gaunant veiklas:", err);
        }
    };

    useEffect(() => {
        fetchPendingActivities();
    }, []);

    const handleApprove = async (activityId) => {
        try {
            const token = localStorage.getItem('token');
            await axios.put(`/api/activities/approve/${activityId}`, {}, {
                headers: { Authorization: `Bearer ${token}` }
            });
            setActivities(prev => prev.filter(a => a.id !== activityId));
        } catch (err) {
            console.error("Klaida patvirtinant veiklą:", err);
        }
    };

    const handleReject = async (activityId) => {
        try {
            const token = localStorage.getItem('token');
            await axios.put(`/api/activities/reject/${activityId}`, {}, {
                headers: { Authorization: `Bearer ${token}` }
            });
            setActivities(prev => prev.filter(a => a.id !== activityId));
        } catch (err) {
            console.error("Klaida atmetant veiklą:", err);
        }
    };

    return (
        <Box>
            <Typography variant="h4" gutterBottom>Veiklų užklausos</Typography>
            <Grid container spacing={3}>
                {activities.length > 0 ? (
                    activities.map(activity => (
                        <Grid item xs={12} sm={6} md={4} key={activity.id}>
                            <ActivityRequestCard
                                activity={activity}
                                onApprove={handleApprove}
                                onReject={handleReject}
                            />
                        </Grid>
                    ))
                ) : (
                    <Typography variant="body1">Nėra laukiančių veiklų.</Typography>
                )}
            </Grid>
        </Box>
    );
};

export default ActivityRequestsTab;