import React, { useState } from 'react';
import { Box, Button, Collapse, Typography } from '@mui/material';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import ExpandLessIcon from '@mui/icons-material/ExpandLess';
import {categories} from "../utils/constants.js";



const InterestSelector = ({ selected, setSelected }) => {
    const [open, setOpen] = useState(false);

    const toggleInterest = (interest) => {
        if (selected.includes(interest)) {
            setSelected(selected.filter(item => item !== interest));
        } else {
            setSelected([...selected, interest]);
        }
    };

    return (
        <Box sx={{ mb: 2 }}>
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                <Typography variant="h5" sx={{textAlign: 'left', mb: 1, mt: 2}}>Pomėgiai</Typography>
                <Button
                    onClick={() => setOpen(!open)}
                    size="small"
                    sx={{ minWidth: 0, p: 0, ml: 'auto' }}
                >
                    {open ? <ExpandLessIcon sx={{ color: '#000' }} /> : <ExpandMoreIcon sx={{ color: '#000' }} />}
                </Button>
            </Box>

            <Collapse in={open}>
                <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
                    {categories.map(({ value, label }) => (
                        <Button
                            key={value}
                            variant={selected.includes(value) ? 'contained' : 'outlined'}
                            color={selected.includes(value) ? 'primary' : 'inherit'}
                            onClick={() => toggleInterest(value)}
                            sx={{
                                borderRadius: '25px',
                                border: '2px solid #422800',
                                color: selected.includes(value) ? '#fff' : '#422800',
                                backgroundColor: selected.includes(value) ? '#ffb15a' : '#fff',
                                fontWeight: 600,
                                '&:hover': {
                                    backgroundColor: selected.includes(value) ? '#f0a143' : '#f7f7f7',
                                },
                            }}
                        >
                            {label}
                        </Button>
                    ))}
                </Box>
            </Collapse>
        </Box>
    );
};

export default InterestSelector;