import React, { useState } from 'react';
import {
    Card, CardContent, Typography, CardActions, Button, TextField,
    Collapse, IconButton, Box
} from '@mui/material';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import ExpandLessIcon from '@mui/icons-material/ExpandLess';

const ProviderRequestCard = ({ request, onApprove, onDecline }) => {
    const [expanded, setExpanded] = useState(false);
    const [rejectionReason, setRejectionReason] = useState('');

    const handleDecline = () => {
        onDecline(request.id, rejectionReason);
        setRejectionReason('');
    };

    return (
        <Card sx={{ mb: 2 }}>
            <CardContent onClick={() => setExpanded(!expanded)} sx={{ cursor: 'pointer' }}>
                <Box display="flex" alignItems="center" justifyContent="space-between">
                    <Typography variant="h6">{request.email}</Typography>
                    <IconButton>
                        {expanded ? <ExpandLessIcon /> : <ExpandMoreIcon />}
                    </IconButton>
                </Box>
                <Typography variant="body2">{request.description}</Typography>
            </CardContent>

            <Collapse in={expanded} timeout="auto" unmountOnExit>
                <CardContent>
                    <Typography variant="body2"><strong>Vardas:</strong> {request.name}</Typography>
                    <Typography variant="body2"><strong>Tel.:</strong> {request.phone}</Typography>
                    {request.website && (
                        <Typography variant="body2">
                            <strong>Website:</strong> <a href={request.website} target="_blank" rel="noreferrer">{request.website}</a>
                        </Typography>
                    )}
                    <Typography variant="body2"><strong>Tipas:</strong> {request.providerType}</Typography>

                    <TextField
                        label="Atmetimo priežastis"
                        value={rejectionReason}
                        onChange={(e) => setRejectionReason(e.target.value)}
                        fullWidth
                        multiline
                        rows={3}
                        sx={{ mt: 2 }}
                    />
                </CardContent>

                <CardActions sx={{ justifyContent: 'flex-end' }}>
                    <Button variant="contained" color="primary" onClick={() => onApprove(request.id)}>
                        Patvirtinti
                    </Button>
                    <Button variant="contained" color="error" onClick={handleDecline}>
                        Atmesti
                    </Button>
                </CardActions>
            </Collapse>
        </Card>
    );
};

export default ProviderRequestCard;