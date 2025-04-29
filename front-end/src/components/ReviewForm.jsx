import React from 'react';
import { TextField, Box, Rating, Typography } from '@mui/material';
import ButtonFunky from './ButtonFunky';

const ReviewForm = ({ comment, setComment, rating, setRating, onSubmit }) => {
    return (
        <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', mt: 5 }}>
            <TextField
                fullWidth
                multiline
                rows={3}
                value={comment}
                onChange={(e) => setComment(e.target.value)}
                placeholder="Rašykite komentarą..."
                sx={{ mb: 2 }}
            />

            <Typography variant="body1" sx={{ mb: 1 }}>
                Įvertinkite veiklą:
            </Typography>

            <Rating
                name="activity-rating"
                value={rating}
                onChange={(event, newValue) => {
                    setRating(newValue);
                }}
                precision={1}
                size="large"
                sx={{ mb: 3 }}
            />

            <ButtonFunky onClick={() => onSubmit(comment, rating)} sx={{ alignSelf: 'center' }}>
                Komentuoti
            </ButtonFunky>
        </Box>
    );
};

export default ReviewForm;