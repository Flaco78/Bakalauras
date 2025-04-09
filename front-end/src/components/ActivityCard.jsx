import React from 'react';
import {CardContent, Typography, CardMedia, Box, CardActionArea} from '@mui/material';
import { Link } from 'react-router-dom';

const ActivityCard = ({ activity }) => {
    return (
        <Box sx={{ mt: 8 }}>
            <CardActionArea
                component={Link}
                to={`/api/activities/${activity.id}`}
                sx={{
                    textDecoration: 'none',
                    padding: 0,
                    maxWidth: 280,
                    backgroundColor: '#ffffff',
                    border: '2px solid #422800',
                    borderRadius: '30px',
                    boxShadow: '4px 4px 0 0 #422800',
                    display: 'flex',
                    flexDirection: 'column',
                    color: '#422800',
                    transition: 'all 0.2s ease-in-out',
                    '&:hover': {
                        backgroundColor: '#fff',
                    },
                    '&:active': {
                        boxShadow: '2px 2px 0 0 #422800',
                        transform: 'translate(2px, 2px)',
                    },

                }}>
                <CardMedia
                    component="img"
                    height="170"
                    image={activity.imageUrl}
                    alt={activity.title}
                    sx={{
                        width: '100%',
                        height: 250,
                        objectFit: 'cover',
                        borderTopLeftRadius: '30px',
                        borderTopRightRadius: '30px',
                    }}
                />
                <CardContent sx={{ padding: 2 }}>
                    <Typography sx={{ fontSize: '16px', fontFamily: 'roboto', fontWeight: '300' }}>
                        {activity.title}
                    </Typography>
                    <Typography>
                        {activity.description}
                    </Typography>
                <Box mt={2}>
                    <Box display="flex" justifyContent="space-between">
                        <Box>
                            <Typography variant="body2" sx={{ fontWeight: 'bold', fontSize: '14px' }}>
                                €{activity.price}
                            </Typography>
                            <Typography variant="body2" color="text.secondary">
                                Per class
                            </Typography>
                        </Box>
                        <Box>
                            <Typography variant="body2" sx={{ fontWeight: 'bold', fontSize: '14px' }}>
                                {activity.location.split(',')[0]}
                            </Typography>
                            <Typography variant="body2" color="text.secondary">
                                Location
                            </Typography>
                        </Box>
                        <Box>
                            <Typography variant="body2" sx={{ fontWeight: 'bold', fontSize: '14px' }}>
                                {activity.durationMinutes} min
                            </Typography>
                            <Typography variant="body2" color="text.secondary">
                                Duration
                            </Typography>
                        </Box>
                    </Box>
                </Box>
                </CardContent>
            </CardActionArea>
        </Box>
    );
};

export default ActivityCard;