import React, { useEffect, useState } from 'react';
import { Typography, Button, Box } from '@mui/material';
import ActivityFormDialog from '../forms/ActivityFormDialog';

import axios from 'axios';
import ActivityTable from "../components/ActivityTable.jsx";
import TimeslotFormDialog from "../forms/TimeslotFormDialog.jsx";
import { Tabs, Tab } from '@mui/material';
import ProviderBookingsTab from "./provider/ProviderBookingsTab.jsx";

const ProviderDashboard = () => {
    const [activities, setActivities] = useState([]);
    const [openForm, setOpenForm] = useState(false);
    const [selectedActivity, setSelectedActivity] = useState(null);
    const [openTimeslotForm, setOpenTimeslotForm] = useState(false);
    const [selectedTab, setSelectedTab] = useState(0);


    const handleTabChange = (event, newValue) => {
        setSelectedTab(newValue);
    };

    const fetchProviderActivities = async () => {
        try {
            const token = localStorage.getItem('token');
            const response = await axios.get('/api/activities/mine', {
                headers: { Authorization: `Bearer ${token}` },
            });
            setActivities(response.data);
        } catch (err) {
            console.error("Klaida gaunant veiklas:", err);
        }
    };

    useEffect(() => {
        fetchProviderActivities();
    }, []);

    const handleSaveActivity = (savedActivity) => {
        setActivities((prev) => {
            const exists = prev.find((a) => a.id === savedActivity.id);
            if (exists) {
                return prev.map((a) => (a.id === savedActivity.id ? savedActivity : a));
            } else {
                return [...prev, savedActivity];
            }
        });
        console.log("Activity saved", savedActivity);
        fetchProviderActivities();
    };
    const handleEdit = (activity) => {
        setSelectedActivity(activity);
        setOpenForm(true);
    };

    const handleDelete = async (activityId) => {
        try {
            const token = localStorage.getItem('token');
            await axios.delete(`/api/activities/${activityId}`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            fetchProviderActivities();
        } catch (err) {
            console.error("Klaida trinant veiklą:", err);
        }
    };

    const handleAddTimeslot = (activity) => {
        setSelectedActivity(activity);
        setOpenTimeslotForm(true);
    };


    return (
        <Box>
            <Tabs value={selectedTab} onChange={handleTabChange} sx={{ mb: 2 }}>
                <Tab label="Veiklos" />
                <Tab label="Užsakymai" />
            </Tabs>
            {selectedTab === 0 && (
                <>
            <Typography variant="h4" gutterBottom>Mano teikiamos veiklos</Typography>
            <Button variant="contained" onClick={() => setOpenForm(true)} sx={{ mb: 2 }}>
                Pridėti veiklą
            </Button>
            <ActivityTable
                activities={activities}
                onEdit={handleEdit}
                onDelete={handleDelete}
                onAddTimeslot={handleAddTimeslot}
            />
            {openForm && (
                <ActivityFormDialog
                    open={openForm}
                    onClose={() => {
                        setOpenForm(false);
                        setSelectedActivity(null);
                        fetchProviderActivities();
                    }}
                    activity={selectedActivity}
                    onSave={handleSaveActivity}
                />
            )}
            {openTimeslotForm && (
                <TimeslotFormDialog
                    open={openTimeslotForm}
                    onClose={() => setOpenTimeslotForm(false)}
                    activity={selectedActivity}
                    onSave={handleSaveActivity}
                />
            )}
                </>
            )}
            {selectedTab === 1 && (
                <ProviderBookingsTab />
            )}

        </Box>
    );
};

export default ProviderDashboard;