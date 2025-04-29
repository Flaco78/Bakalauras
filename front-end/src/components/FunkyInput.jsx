import React from 'react';
import { InputBase, Box, Typography } from '@mui/material';

const FunkyInput = ({ label, value, onChange, placeholder, sx = {}, ...props }) => {
    return (
        <Box>
            {/* Rodome label'į */}
            {label && (
                <Typography variant="h6" sx={{ textAlign: 'center', mb: 1, color: '#422800' }}>
                    {label}
                </Typography>
            )}

            {/* Įvedimo laukelis */}
            <Box
                sx={{
                    border: '2px solid #422800',
                    borderRadius: '30px',
                    boxShadow: '4px 4px 0 0 #422800',
                    color: '#422800',
                    fontWeight: 600,
                    fontSize: '18px',
                    padding: '0 18px',
                    lineHeight: '50px',
                    textAlign: 'left',
                    display: 'inline-block',
                    maxWidth: '120px',
                    backgroundColor: 'transparent',
                    '&:hover': {
                        backgroundColor: '#fff',
                    },
                    ...sx,
                }}
            >
                <InputBase
                    value={value}
                    onChange={onChange}
                    placeholder={placeholder}
                    fullWidth
                    sx={{
                        color: '#422800',
                        fontWeight: 600,
                        fontSize: '18px',
                        border: 'none',
                    }}
                    {...props}
                />
            </Box>
        </Box>
    );
};

export default FunkyInput;