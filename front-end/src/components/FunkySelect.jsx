import React from 'react';
import { Box, Typography, MenuItem, Select } from '@mui/material';

const FunkySelect = ({ label, value, onChange, options = [], children, sx = {}, ...props }) => {
    return (
        <Box>
            {label && (
                <Typography variant="h6" sx={{ textAlign: 'center', mb: 1, color: '#422800' }}>
                    {label}
                </Typography>
            )}

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
                    minWidth: '150px',
                    backgroundColor: 'transparent',
                    '&:hover': {
                        backgroundColor: '#fff',
                    },
                    ...sx,
                }}
            >
                <Select
                    value={value}
                    onChange={onChange}
                    displayEmpty
                    fullWidth
                    variant="standard"
                    disableUnderline
                    sx={{
                        color: '#422800',
                        fontWeight: 600,
                        fontSize: '18px',
                        minWidth: 150,
                        backgroundColor: 'transparent',
                        '& .MuiSelect-select': {
                            padding: '0 12px',
                        },
                    }}
                    {...props}
                >
                    {children && React.Children.count(children) > 0
                        ? children
                        : Array.isArray(options) && options.map((option) => (
                        <MenuItem key={option.value} value={option.value}>
                            {option.label}
                        </MenuItem>
                    ))
                    }
                </Select>
            </Box>
        </Box>
    );
};

export default FunkySelect;