import React from 'react';
import { Box, Typography } from '@mui/material';
import SansTypography from "./SansTypography.jsx";

const TwoColumnLayout = ({ description, children, rightContent }) => {
    return (

        <Box sx={{ display: 'flex', mt:2, mb:2, flexDirection: { xs: 'column', md: 'row' } }}>
            <Box sx={{ flex: 1}}>
                <SansTypography variant="h6">
                    Apie veiklą
                </SansTypography>
                <Typography variant="h6" sx={{ mb: 3, textAlign: 'left', mr: 2}}>{description}</Typography>
                {children}
            </Box>

            <Box sx={{ flex: 1 }}>
                {rightContent}
            </Box>
        </Box>
    );
};

export default TwoColumnLayout;