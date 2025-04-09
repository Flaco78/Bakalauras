import React from 'react';
import { Avatar } from '@mui/material';

const UserAvatar = ({ name, imageUrl, size = 80 }) => {
    const initials = name ? name.split(' ').map(n => n[0]).join('').toUpperCase() : 'U';

    return (
        <Avatar
            src={imageUrl}
            sx={{
                width: size,
                height: size,
                fontSize: size / 2.5,
                bgcolor: '#ffb15a',
                color: '#422800'
            }}
        >
            {!imageUrl && initials}
        </Avatar>
    );
};

export default UserAvatar;