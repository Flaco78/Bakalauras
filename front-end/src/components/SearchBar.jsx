import React from 'react';
import { Box, InputBase } from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import {useNavigate} from "react-router-dom";

const SearchBarFunky = ({ onChange, value, placeholder = "Rašykite čia...", sx, ...props }) => {
    const navigate = useNavigate();

    const handleKeyPress = (event) => {
        if (event.key === 'Enter') {
            event.preventDefault();
            if (value.trim()) {
                navigate(`/search?query=${encodeURIComponent(value.trim())}`);
            } else {
                navigate('/search');
            }
        }
    };

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
                '&:hover': {
                    backgroundColor: '#fff',
                },
                '&:active': {
                    boxShadow: '2px 2px 0 0 #422800',
                    transform: 'translate(2px, 2px)',
                },
                ...sx,
            }}
        >
            <SearchIcon sx={{ color: '#422800', mr: 1 }} />
            <InputBase
                onChange={onChange}
                value={value}
                onKeyDown={handleKeyPress}
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