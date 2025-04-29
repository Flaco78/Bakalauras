import React, { useState } from 'react';
import {
    Card, CardContent, Typography, CardActions, Button, Collapse, Box, IconButton
} from '@mui/material';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import ExpandLessIcon from '@mui/icons-material/ExpandLess';

const ActivityRequestCard = ({ activity, onApprove, onReject }) => {
    const [expanded, setExpanded] = useState(false);

    return (
        <Card sx={{ mb: 2 }}>
            <CardContent onClick={() => setExpanded(!expanded)} sx={{ cursor: 'pointer' }}>
                <Box display="flex" justifyContent="space-between" alignItems="center">
                    <Typography variant="h6">{activity.name}</Typography>
                    <IconButton>{expanded ? <ExpandLessIcon /> : <ExpandMoreIcon />}</IconButton>
                </Box>
                <Typography variant="body2">{activity.descriptionChild}</Typography>
            </CardContent>

            <Collapse in={expanded} timeout="auto" unmountOnExit>
                <CardContent>
                    <Typography variant="body2"><strong>Kategorija:</strong> {activity.category}</Typography>
                    <Typography variant="body2"><strong>Trukmė:</strong> {activity.durationMinutes} min</Typography>
                    <Typography variant="body2"><strong>Kaina:</strong> {activity.price} € ({activity.priceType})</Typography>
                    <Typography variant="body2"><strong>Lokacija:</strong> {activity.location}</Typography>
                    <Typography variant="body2"><strong>Pristatymas:</strong> {activity.deliveryMethod}</Typography>
                    <Typography variant="body2"><strong>Tiekėjas:</strong> {activity.provider?.email || 'Nežinomas'}</Typography>
                </CardContent>
                <CardActions sx={{ justifyContent: 'flex-end' }}>
                    <Button variant="contained" color="primary" onClick={() => onApprove(activity.id)}>
                        Patvirtinti
                    </Button>
                    <Button variant="contained" color="error" onClick={() => onReject(activity.id)}>
                        Atmesti
                    </Button>
                </CardActions>
            </Collapse>
        </Card>
    );
};

export default ActivityRequestCard;