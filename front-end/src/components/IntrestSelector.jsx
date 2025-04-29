import React, { useState } from 'react';
import { Box, Button, Collapse, Typography } from '@mui/material';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import ExpandLessIcon from '@mui/icons-material/ExpandLess';

const interests = [
    'SPORTS', 'MUSIC', 'ART', 'SCIENCE', 'CODING', 'DANCE'
];

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
                <Typography variant="h5" sx={{textAlign: 'left', mb: 1, mt: 2}}>Intrests</Typography>
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
                    {interests.map((interest) => (
                        <Button
                            key={interest}
                            variant={selected.includes(interest) ? 'contained' : 'outlined'}
                            color={selected.includes(interest) ? 'primary' : 'inherit'}
                            onClick={() => toggleInterest(interest)}
                            sx={{
                                borderRadius: '25px',
                                border: '2px solid #422800',
                                color: selected.includes(interest) ? '#fff' : '#422800',
                                backgroundColor: selected.includes(interest) ? '#ffb15a' : '#fff',
                                fontWeight: 600,
                                '&:hover': {
                                    backgroundColor: selected.includes(interest) ? '#f0a143' : '#f7f7f7',
                                },
                            }}
                        >
                            {interest.charAt(0) + interest.slice(1).toLowerCase()}
                        </Button>
                    ))}
                </Box>
            </Collapse>
        </Box>
    );
};

export default InterestSelector;