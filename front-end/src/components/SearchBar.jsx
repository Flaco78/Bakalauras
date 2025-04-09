import React from 'react';
import { Box, InputBase } from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';

const SearchBarFunky = ({ onChange, value, placeholder = "Search...", sx, ...props }) => {
    return (
        <Box
            sx={{
                display: 'flex',
                alignItems: 'center',
                backgroundColor: '#fff',
                border: '2px solid #422800',
                borderRadius: '30px',
                boxShadow: '4px 4px 0 0 #422800',
                padding: '0 12px',
                width: '100%',
                height: '50px',
                ...sx,
            }}
        >
            <SearchIcon sx={{ color: '#422800', mr: 1 }} />
            <InputBase
                onChange={onChange}
                value={value}
                placeholder={placeholder}
                inputProps={{ 'aria-label': 'search' }}
                sx={{
                    flex: 1,
                    fontWeight: 'bold',
                    fontSize: '16px',
                    color: '#422800',
                }}
                {...props}
            />
        </Box>
    );
};

export default SearchBarFunky;