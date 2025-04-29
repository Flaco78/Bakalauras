import React from 'react';
import { Avatar } from '@mui/material';

const stringToHslColor = (str, s = 70, l = 60) => {
    let hash = 0;
    for (let i = 0; i < str.length; i++) {
        hash = str.charCodeAt(i) + ((hash << 5) - hash);
    }
    const h = hash % 360;
    return `hsl(${h}, ${s}%, ${l}%)`;
};

const UserAvatar = ({ name, imageUrl, size = 80 }) => {
    const initials = name ? name.split(' ').map(n => n[0]).join('').toUpperCase() : 'U';
    const bgColor = stringToHslColor(name);

    return (
        <Avatar
            src={imageUrl}
            sx={{
                width: size,
                height: size,
                fontSize: size / 2.5,
                bgcolor: imageUrl ? 'transparent' : bgColor,
                color: '#fff',
            }}
        >
            {!imageUrl && initials}
        </Avatar>
    );
};

export default UserAvatar;