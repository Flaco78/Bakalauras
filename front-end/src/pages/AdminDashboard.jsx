import React, { useState } from 'react';
import { Box, Tabs, Tab, Typography } from '@mui/material';
import UsersTab from './admin/UserTab.jsx';
import ActivitiesTab from './admin/ActivitiesTab.jsx';
import ProviderRequestsTab from './admin/ProviderRequestsTab.jsx';
import ActivityRequestsTab from './admin/ActivityRequestsTab';
import ProviderTab from "./admin/ProviderTab";

const AdminDashboard = () => {
    const [tabIndex, setTabIndex] = useState(0);

    const handleTabChange = (event, newValue) => {
        setTabIndex(newValue);
    };

    return (
        <Box sx={{ mt: 10, px: 4 }}>
            <Typography variant="h4" sx={{ mb: 2 }}>Administratoriaus langas</Typography>
            <Box sx={{ display: 'flex', justifyContent: 'center' }}>
                <Tabs
                    value={tabIndex}
                    onChange={handleTabChange}
                    variant="scrollable"
                    scrollButtons="auto"
                >
                    <Tab label="Vartotojai" />
                    <Tab label="Veiklos" />
                    <Tab label="Tiekėjai" />
                    <Tab label="Tiekėjų užklausos" />
                    <Tab label="Veiklų užklausos" />
                </Tabs>
            </Box>

            <Box sx={{ mt: 4 }}>
                {tabIndex === 0 && <UsersTab />}
                {tabIndex === 1 && <ActivitiesTab />}
                {tabIndex === 2 && <ProviderTab />}
                {tabIndex === 3 && <ProviderRequestsTab />}
                {tabIndex === 4 && <ActivityRequestsTab />}
            </Box>
        </Box>
    );
};

export default AdminDashboard;