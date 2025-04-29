import React from 'react';
import { Typography } from '@mui/material';

const SansTypography = ({ children, variant = "h2", sx = {}, ...props }) => {
    return (
        <Typography
            variant={variant}
            sx={{
                fontFamily: '"Comic Sans MS", cursive, sans-serif',
                fontWeight: 'bold',
                color: 'rgba(0,0,0,0.87)',
                textAlign: 'left',
                ...sx,
            }}
            {...props}
        >
            {children}
        </Typography>
    );
};

export default SansTypography;