import React, { useState, useEffect } from 'react';
import {Typography, Grid, Box} from '@mui/material';
import axios from 'axios';
import { Button, Collapse } from '@mui/material';
import ProviderRequestCard from '../../forms/ProviderRequestCard';

const ProviderRequestsTab = () => {
    const [providerRequests, setProviderRequests] = useState([]);
    const [showRejected, setShowRejected] = useState(false);
    const [rejectedRequests, setRejectedRequests] = useState([]);

    const toggleShowRejected = async () => {
        setShowRejected(prev => !prev);
        if (!showRejected && rejectedRequests.length === 0) {
            try {
                const token = localStorage.getItem('token');
                const response = await axios.get('/api/provider-request?status=REJECTED', {
                    headers: { Authorization: `Bearer ${token}` }
                });
                setRejectedRequests(response.data);
            } catch (error) {
                console.error("Error fetching rejected requests:", error);
            }
        }
    };

    useEffect(() => {
        const fetchProviderRequests = async () => {
            try {
                const token = localStorage.getItem('token');
                const response = await axios.get('/api/provider-request/all', {
                    headers: { Authorization: `Bearer ${token}` }
                });
                setProviderRequests(response.data.filter(req => req.status === 'PENDING'));
            } catch (error) {
                console.error("Error fetching provider requests:", error);
            }
        };

        fetchProviderRequests();
    }, []);

    const approveRequest = async (requestId) => {
        try {
            const token = localStorage.getItem('token');
            await axios.patch(`/api/provider/approve/${requestId}`, {}, {
                headers: { Authorization: `Bearer ${token}` }
            });
            setProviderRequests(prev => prev.filter(req => req.id !== requestId));
        } catch (error) {
            console.error("Error approving provider request:", error);
        }
    };

    const declineRequest = async (requestId, reason) => {
        try {
            const token = localStorage.getItem('token');
            await axios.patch(`/api/provider/decline/${requestId}`, { reason }, {
                headers: { Authorization: `Bearer ${token}` }
            });
            setProviderRequests(prev => prev.filter(req => req.id !== requestId));
        } catch (error) {
            console.error("Error declining provider request:", error);
        }
    };

    return (
        <Box>
            <Typography variant="h4" gutterBottom>Tiekėjų užklausos</Typography>
            <Grid container spacing={3}>
                {providerRequests.length > 0 ? (
                    providerRequests.map(request => (
                        <Grid item xs={12} sm={6} md={4} key={request.id}>
                            <ProviderRequestCard
                                request={request}
                                onApprove={approveRequest}
                                onDecline={declineRequest}
                            />
                        </Grid>
                    ))
                ) : (
                    <Typography variant="body1">Nėra laukiančių užklausų.</Typography>
                )}
            </Grid>
            <Box mt={4}>
                <Button onClick={toggleShowRejected} variant="outlined">
                    {showRejected ? "Slėpti atmestas užklausas" : "Peržiūrėti atmestas užklausas"}
                </Button>

                <Collapse in={showRejected}>
                    <Box mt={2}>
                        <Typography variant="h5" gutterBottom>Atmestos užklausos</Typography>
                        <Grid container spacing={3}>
                            {rejectedRequests.length > 0 ? (
                                rejectedRequests.map(request => (
                                    <Grid item xs={12} sm={6} md={4} key={request.id}>
                                        <ProviderRequestCard request={request} disabled />
                                    </Grid>
                                ))
                            ) : (
                                <Typography variant="body1">Nėra atmestų užklausų.</Typography>
                            )}
                        </Grid>
                    </Box>
                </Collapse>
            </Box>
        </Box>
    );
};

export default ProviderRequestsTab;