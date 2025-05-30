import React from 'react';
import {CardContent, Typography, CardMedia, Box, CardActionArea, Divider} from '@mui/material';
import { Link } from 'react-router-dom';
import {priceTypes} from "../utils/constants.js";

const getDisplayLocation = (location) => {
    if (!location) return 'Online';
    const quoteMatch = location.match(/["„”](.*?)["’’]/);
    if (quoteMatch && quoteMatch[1]) {
        return quoteMatch[1].trim();
    }
    return location.split(',')[0].trim();
};

const ActivityCard = ({ activity }) => {
    const getPriceTypeLabel = (priceTypeValue) => {
        const priceType = priceTypes.find(pt => pt.value === priceTypeValue);
        return priceType ? priceType.label : priceTypeValue;
    };
    return (
        <Box sx={{ mt: 3, width: 300 }}>
            <CardActionArea
                component={Link}
                to={`/activities/${activity.id}`}
                sx={{
                    textDecoration: 'none',
                    padding: 0,
                    height: 420,
                    width: '100%',
                    backgroundColor: '#ffffff',
                    border: '2px solid #422800',
                    borderRadius: '30px',
                    boxShadow: '4px 4px 0 0 #422800',
                    display: 'flex',
                    flexDirection: 'column',
                    justifyContent: 'space-between', // kad "kaina" ir kita būtų apačioje
                    color: '#422800',
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
                <CardMedia
                    component="img"
                    height="170"
                    image={activity.imageUrl}
                    alt={activity.title}
                    sx={{
                        width: '100%',
                        height: 200,
                        objectFit: 'cover',
                        borderTopLeftRadius: '30px',
                        borderTopRightRadius: '30px',
                    }}
                />
                <CardContent sx={{ padding: 2 }}>
                    <Box sx={{height: 110}}>
                    <Typography sx={{ fontSize: '20px', fontWeight: '500', }}>
                        {activity.title}
                    </Typography>
                    <Typography sx = {{mb: 2, fontSize: '15px', fontWeight: '300'}}>
                        {activity.descriptionChild}
                    </Typography>
                    </Box>
                <Divider />
                <Box sx ={{mt:1, }}>
                    <Box display="flex" justifyContent="space-between">
                        <Box>
                            <Typography variant="body2" sx={{ fontWeight: 'bold', fontSize: '14px' }}>
                                €{activity.price}
                            </Typography>
                            <Typography variant="body2" color="text.secondary">
                                {getPriceTypeLabel(activity.priceType)}
                            </Typography>
                        </Box>
                        <Box>
                            <Typography variant="body2" sx={{ fontWeight: 'bold', fontSize: '14px' }}>
                                {getDisplayLocation(activity.location)}
                            </Typography>
                            <Typography variant="body2" color="text.secondary">
                                Vieta
                            </Typography>
                        </Box>
                        <Box>
                            <Typography variant="body2" sx={{ fontWeight: 'bold', fontSize: '14px' }}>
                                {activity.durationMinutes} min
                            </Typography>
                            <Typography variant="body2" color="text.secondary">
                                Laikas
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