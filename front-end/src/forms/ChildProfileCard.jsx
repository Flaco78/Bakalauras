import React, {useState} from 'react';
import { Card, CardContent, Typography, Avatar, Button, Box, IconButton, MenuItem, Menu } from '@mui/material';
import MoreVertIcon from '@mui/icons-material/MoreVert';
import {genderOptions} from "../utils/constants.js";

const genderImages = {
    MALE: "b4bbb43c991a660d15d0ff6cf8e62e7a.png",
    FEMALE: "502bd0c1977a1fd87976bd431ddb6c62.png",
    OTHER: "e1ec649d5775f68b32b93504a5969af9.png",
};

const ChildProfileCard = ({ child, onEdit, onDelete }) => {
    const { name, birthDate, gender } = child;
    const image = genderImages[gender] || genderImages.OTHER;
    const [anchorEl, setAnchorEl] = useState(null);
    const open = Boolean(anchorEl);

    const handleMenuClick = (event) => {
        setAnchorEl(event.currentTarget);
    };

    const handleClose = () => {
        setAnchorEl(null);
    };

    const handleDelete = () => {
        handleClose();
        onDelete(child.id);
    };

    const calculateAge = (birthDate) => {
        const birth = new Date(birthDate);
        const today = new Date();
        let age = today.getFullYear() - birth.getFullYear();
        const m = today.getMonth() - birth.getMonth();
        if (m < 0 || (m === 0 && today.getDate() < birth.getDate())) {
            age--;
        }
        return age;
    };

    return (
        <Card
            sx={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
                width: '100%',
                mb: 2,
                p: 2,
                backgroundColor: '#ffffff',
                border: '2px solid #422800',
                borderRadius: '30px',
                boxShadow: '4px 4px 0 0 #422800',
                transition: 'all 0.2s ease-in-out',
                '&:hover': {
                    backgroundColor: '#fff',
                },
                '&:active': {
                    boxShadow: '2px 2px 0 0 #422800',
                    transform: 'translate(2px, 2px)',
                },
            }}
        >
            <Box sx={{ display: 'flex', alignItems: 'center' }}>
                <Avatar
                    src={image}
                    alt={gender}
                    sx={{
                        width: 150,
                        height: 150,
                    }}
                />
                <CardContent sx={{ p: 0, pt: 3, textAlign: 'left' }}>
                    <Typography variant="h6" fontWeight="bold">
                        {name}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                        Metai: {calculateAge(birthDate)}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                        Lytis: {genderOptions.find(opt => opt.value === gender)?.label || 'Kita'}
                    </Typography>
                </CardContent>
            </Box>

            <Box sx={{ display: 'flex', alignItems: 'center', ml: 'auto' }}>
            <Button
                variant="contained"
                onClick={() => onEdit(child)}
                sx={{
                    minWidth: 60,
                    width: 60,
                    height: 60,
                    backgroundColor: '#ffb15a', // Šviesus oranžinis atspalvis
                    border: '2px solid #422800', // Tamsesnis rudas apvadas
                    borderRadius: '30px', // Apvalūs kampai
                    boxShadow: '4px 4px 0 0 #422800', // Šviesus rudas šešėlis
                    color: '#422800', // Tamsiai ruda spalva
                    cursor: 'pointer',
                    display: 'flex', // <- čia svarbiausia
                    alignItems: 'center', // <- centruojam vertikaliai
                    justifyContent: 'center',
                    fontWeight: 600,
                    fontSize: '18px',
                    padding: '0 18px',
                    lineHeight: '50px',
                    textAlign: 'center',
                    textDecoration: 'none',
                    userSelect: 'none',
                    touchAction: 'manipulation',

                    '&:hover': {
                        backgroundColor: '#fff',
                    },
                    '&:active': {
                        boxShadow: '2px 2px 0 0 #422800',
                        transform: 'translate(2px, 2px)',
                    },
                }}
            >
                <img src="/pencil-edit-button.svg" alt="underline" style={{width: '20px'}}/>
            </Button>
            <IconButton
                aria-controls="child-menu"
                aria-haspopup="true"
                onClick={handleMenuClick}
                sx={{ marginLeft: 2 }}
            >
                <MoreVertIcon />
            </IconButton>
            <Menu
                anchorEl={anchorEl}
                open={open}
                onClose={handleClose}
            >
                <MenuItem onClick={handleDelete}>Ištrinti</MenuItem>
            </Menu>
        </Box>
    </Card>
    );
};

export default ChildProfileCard;