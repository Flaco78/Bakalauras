import React, {useEffect, useRef, useState} from 'react';
import { useParams } from 'react-router-dom';
import {
    Box,
    Typography,
    CircularProgress,
    Divider,
    DialogTitle,
    DialogContent,
    DialogContentText, DialogActions, Dialog, Button, Rating
} from '@mui/material';
import axios from 'axios';
import ButtonFunky from "../components/ButtonFunky.jsx";
import TwoColumnLayout from "../components/TwoColumnLayout.jsx";
import UserAvatar from "../components/UserAvatar.jsx";
import SansTypography from "../components/SansTypography.jsx";
import ReviewForm from "../components/ReviewForm.jsx";
import {Grid} from "@mui/system";
import { useChild } from '../context/ChildContext.jsx';
import FavoriteIcon from '@mui/icons-material/Favorite';
import FavoriteBorderIcon from '@mui/icons-material/FavoriteBorder';
import {priceTypes} from "../utils/constants.js";

const ActivityCardDetails = () => {
    const { id } = useParams();
    const [activity, setActivity] = useState(null);
    const [loading, setLoading] = useState(true);
    const [reviews, setReviews] = useState([]);
    const [comment, setComment] = useState("");
    const [rating, setRating] = useState(0);
    const [openModal, setOpenModal] = useState(false);
    const [selectedReview, setSelectedReview] = useState(null);
    const { selectedChildId } = useChild();
    const [isFavorited, setIsFavorited] = useState(false);
    const [timeslots, setTimeslots] = useState([]);
    const [selectedTimeslot, setSelectedTimeslot] = useState(null);
    const timeslotRef = useRef(null);

    useEffect(() => {
        const token = localStorage.getItem('token');
        const config = {
            headers: {
                Authorization: `Bearer ${token}`
            }
        };

        axios.get(`/api/activities/${id}`, config)
            .then(response => {
                setActivity(response.data);
                setLoading(false);
            })
            .catch(error => {
                console.error("Error fetching activity:", error);
                setLoading(false);
            });
    }, [id]);

    useEffect(() => {
        const token = localStorage.getItem('token');
        const config = {
            headers: { Authorization: `Bearer ${token}` }
        };

        axios.get(`/api/timeslots/activity/${id}`, config)
            .then(response => {
                setTimeslots(Array.isArray(response.data) ? response.data : []);
            })
            .catch(error => {
                console.error("Error fetching timeslots:", error);
            });
    }, [id]);

    useEffect(() => {
        const token = localStorage.getItem('token');
        const config = {
            headers: {
                Authorization: `Bearer ${token}`
            }
        };

        axios.get(`/api/reviews/activity/${id}`, config)
            .then(response => {
                setReviews(response.data);
            })
            .catch(error => {
                console.error("Error fetching reviews:", error);
            });
    }, [id]);

    useEffect(() => {
        if (selectedChildId && activity?.id) {
            const token = localStorage.getItem('token');
            const config = {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            };

            axios.post('/api/interactions', {
                childId: selectedChildId,
                activityId: activity.id,
                interactionType: 'VIEW'
            }, config)
                .then(() => {
                    console.log(`✅ Registered view for activity ${activity.title}`);
                })
                .catch(err => {
                    console.error("❌ Failed to record view interaction:", err);
                });
        }
    }, [selectedChildId, activity?.id]);

    const handleAddReview = (comment, rating) => {
        const token = localStorage.getItem('token');
        const config = {
            headers: {
                Authorization: `Bearer ${token}`
            }
        };

        const reviewData = {
            comment,
            rating,
            activity: { id: activity.id }
        };

        axios.post('/api/reviews/create', reviewData, config)
            .then(response => {
                setReviews([...reviews, response.data]);
                setComment("");
                setRating(0);
            })
            .catch(error => {
                console.error("Error posting review:", error);
            });
    };

    const toggleFavorite = () => {
        const token = localStorage.getItem('token');
        const config = {
            headers: { Authorization: `Bearer ${token}` }
        };

        axios.post('/api/interactions/favorite', {
            childId: selectedChildId,
            activityId: activity.id
        }, config)
            .then(() => {
                setIsFavorited(!isFavorited);
            })
            .catch(err => {
                console.error("❌ Failed to toggle favorite:", err);
            });
    };

    const handleBooking = () => {
        console.log("selectedChildId:", selectedChildId);
        console.log("selectedTimeslot:", selectedTimeslot.id);
        if (!selectedChildId || !selectedTimeslot || !selectedTimeslot.id) {
            console.error("❌ Child or timeslot not selected properly");
            alert("Pasirinkite vaiką ir laiką!");
            return;
        }

        const token = localStorage.getItem('token');
        const config = { headers: { Authorization: `Bearer ${token}` } };

        axios.post('/api/bookings/create', {
            childId: selectedChildId,
            timeSlotId: selectedTimeslot.id
        }, config)
            .then(() => {
                setTimeslots(prev => {
                    const updated = prev.map(t =>
                        t.id === selectedTimeslot.id
                            ? { ...t, currentParticipants: t.currentParticipants + 1 }
                            : t
                    );
                    const updatedSelected = updated.find(t => t.id === selectedTimeslot.id);
                    setSelectedTimeslot(updatedSelected);
                    return updated;
                });

                axios.post('/api/interactions', {
                    childId: selectedChildId,
                    activityId: activity.id,
                    interactionType: 'REGISTER'
                }, config)
                    .then(() => {
                        console.log("📘 Booking interaction recorded!");
                    })
                    .catch(err => console.error("⚠️ Couldn't record booking interaction:", err));

                alert("✅ Užsakymas sėkmingas!");
            })
            .catch((err) => {
                console.error("❌ Klaida užsakant veiklą:", err);
                alert("Nepavyko užsakyti. Galbūt laikas jau užimtas?");
            });
    };

    useEffect(() => {
        if (selectedChildId && activity?.id) {
            const token = localStorage.getItem('token');
            const config = { headers: { Authorization: `Bearer ${token}` } };

            axios.get(`/api/interactions/is-favorited?childId=${selectedChildId}&activityId=${activity.id}`, config)
                .then(res => setIsFavorited(res.data))
                .catch(err => console.error("Couldn't fetch favorite status:", err));
        }
    }, [selectedChildId, activity?.id]);

    const getPriceTypeLabel = (priceTypeValue) => {
        const priceType = priceTypes.find(pt => pt.value === priceTypeValue);
        return priceType ? priceType.label : priceTypeValue;
    };

    const getCategoryEmoji = (category) => {
        switch (category) {
            case 'EDUCATION': return '📚';
            case 'ART': return '🎨';
            case 'MUSIC': return '🎵';
            case 'SPORTS': return '🏀';
            case 'DANCE': return '💃';
            case 'TECH': return '💻';
            case 'SCIENCE': return '🔬';
            case 'NATURE': return '🌿';
            case 'THEATER': return '🎭';
            case 'LANGUAGE': return '🌍';
            case 'MATH': return '➗';
            case 'COOKING': return '🍳';
            case 'CRAFTS': return '✂️';
            case 'OUTDOOR': return '🏕️';
            case 'OTHER': return '🧩';
            default: return '❓';
        }
    };

    if (loading) return <CircularProgress />;

    if (!activity) return <Typography>Veikla nerasta arba neegzistuoja</Typography>;

    return (
        <Box sx={{ p: 4,  mx: 'auto', mt: 4 }}>

            <Box sx={{ display: 'flex', flexDirection: { xs: 'column', md: 'row' }, gap: 3 }}>

                    <Box >
                        <SansTypography>{activity.title}</SansTypography>

                        <TwoColumnLayout
                            description={activity.description}
                            rightContent={
                                <Box sx={{mt:2}}>
                                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 2 }}>
                                        <UserAvatar size={50} name={activity.providerName || 'Provider'} />
                                        <Typography sx={{ textAlign: 'left' }}>
                                            {activity.providerName || 'N/A'}
                                        </Typography>
                                    </Box>
                                    <Typography sx={{ textAlign: 'left' }}>
                                        {activity.providerDescription || 'N/A'}
                                    </Typography>
                                </Box>
                            }
                        />

                        {reviews.length > 0 && (
                            <Box sx={{ mt: 4 }}>
                                <SansTypography variant="h4" sx={{ mb: 2 }}>
                                    Atsiliepimai
                                </SansTypography>
                                <Dialog open={openModal} onClose={() => setOpenModal(false)} maxWidth="sm" fullWidth>
                                    <DialogTitle>Pilnas atsiliepimas</DialogTitle>
                                    <DialogContent>
                                        <DialogContentText>
                                            {selectedReview?.comment}
                                        </DialogContentText>
                                    </DialogContent>
                                    <DialogActions>
                                        <ButtonFunky onClick={() => setOpenModal(false)}>Uždaryti</ButtonFunky>
                                    </DialogActions>
                                </Dialog>

                                <Grid container spacing={3} alignItems="stretch">
                                    {reviews.map((review) => (
                                        <Grid key={review.id}>
                                            <Box
                                                sx={{
                                                    height: '160px',
                                                    overflow: 'hidden',
                                                    display: 'flex',
                                                    width: '357px',
                                                    flexDirection: 'column',
                                                    justifyContent: 'space-between',
                                                    p: 3,
                                                    backgroundColor: '#f9f9f9',
                                                    border: '2px solid #422800',
                                                    borderRadius: '30px',
                                                    position: 'relative',
                                                }}
                                            >
                                                <Box>
                                                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 3 }}>
                                                        <UserAvatar size={50} name={review.user.email || 'Vartotojas'} />
                                                        <SansTypography variant="h6" sx={{ textAlign: 'left' }}>
                                                            {review.user.email}
                                                        </SansTypography>
                                                    </Box>
                                                    <Typography variant="body2" sx={{ fontStyle: 'italic' }}>
                                                        <Rating name="read-only" value={review.rating} readOnly precision={1} />
                                                    </Typography>
                                                    <Typography
                                                        variant="body1"
                                                        sx={{ mt: 2 }}
                                                    >
                                                        {review.comment.length > 50
                                                            ? `${review.comment.slice(0, 60)}...`
                                                            : review.comment}
                                                    </Typography>
                                                </Box>
                                                {review.comment.length > 49 && (
                                                    <Box sx={{ textAlign: 'right', mt: 1 }}>
                                                        <Button
                                                            size="small"
                                                            onClick={() => {
                                                                setSelectedReview(review);
                                                                setOpenModal(true);
                                                            }}
                                                            sx={{
                                                                color: '#f59e0b',
                                                            }}
                                                        >
                                                            Rodyti daugiau
                                                        </Button>
                                                    </Box>
                                                )}
                                            </Box>
                                        </Grid>
                                    ))}
                                </Grid>
                            </Box>

                        )}
                        <ReviewForm
                            comment={comment}
                            setComment={setComment}
                            rating={rating}
                            setRating={setRating}
                            onSubmit={handleAddReview}
                        />
                        <Divider sx={{mt:4}}/>
                        <Box>
                            <SansTypography variant="h6" sx={{ mb: 2, mt:2, textAlign: 'center' }}>Pasirinkite laiką</SansTypography>
                            <Box ref={timeslotRef}>
                                {timeslots.length === 0 ? (
                                    <Typography variant="body1" sx={{ fontStyle: 'italic', color: 'gray', mt: 1 }}>
                                        Nėra laikų šiai veiklai
                                    </Typography>
                                ) : (
                                    timeslots.map((timeslot) => (
                                        <Button
                                            key={timeslot.id}
                                            onClick={() => setSelectedTimeslot(timeslot)}
                                            variant="contained"
                                            sx={{
                                                backgroundColor: selectedTimeslot?.id === timeslot.id ? '#68aa51' : '#fff',
                                                color: selectedTimeslot?.id === timeslot.id ? '#fff' : '#422800',
                                                border: '2px solid #422800',
                                                borderRadius: '20px',
                                                fontWeight: 'bold',
                                                textTransform: 'none',
                                                px: 3,
                                                py: 1.5,
                                                mb: 2,
                                                mr: 2,
                                                boxShadow: selectedTimeslot?.id === timeslot.id ? '4px 4px 0 0 #422800' : 'none',
                                                '&:hover': {
                                                    backgroundColor: '#ffb15a',
                                                    boxShadow: '2px 2px 0 0 #422800',
                                                },
                                                '&:disabled': {
                                                    backgroundColor: '#f0f0f0',
                                                    color: '#aaa',
                                                    border: '2px dashed #ccc',
                                                }
                                            }}
                                            disabled={timeslot.currentParticipants >= timeslot.maxParticipants}
                                        >
                                            🗓️ {new Date(timeslot.startDateTime).toLocaleDateString('lt-LT')}
                                            <br />
                                            🕓 {new Date(timeslot.startDateTime).toLocaleTimeString('lt-LT', { hour: '2-digit', minute: '2-digit' })} - {new Date(timeslot.endDateTime).toLocaleTimeString('lt-LT', { hour: '2-digit', minute: '2-digit' })}
                                        </Button>

                                    ))

                                )}
                                <Typography variant="body2" sx={{ mt: 1, fontWeight: 'bold', color: '#422800' }}>
                                    🧒 Liko vietų: {selectedTimeslot ? selectedTimeslot.maxParticipants - selectedTimeslot.currentParticipants : '-'}
                                </Typography>
                                {selectedTimeslot && (
                                    <ButtonFunky
                                        variant="contained"
                                        color="success"
                                        sx={{ mt: 2 }}
                                        onClick={handleBooking}
                                    >
                                        Užsakyti pasirinktą laiką
                                    </ButtonFunky>
                                )}
                            </Box>
                        </Box>
                        <Divider sx={{mt:4}}/>
                    </Box>

                {/* Dešinysis šonas (sticky kortelė) */}

                <Box
                    sx={{
                        width: '350px',
                        minWidth: '350px',
                        maxWidth: '350px',
                        position: 'sticky',
                        top: 100,
                        alignSelf: 'flex-start',
                    }}
                >

                <Box
                    sx={{
                        p: 3,
                        mb: 3,
                        backgroundColor: '#ffffff',
                        border: '2px solid #422800',
                        borderRadius: '30px',
                        boxShadow: '4px 4px 0 0 #422800',
                        transition: 'all 0.2s ease-in-out',
                        '&:hover': {
                            backgroundColor: '#fff',
                            boxShadow: '2px 2px 0 0 #422800',
                            transform: 'translate(2px, 2px)',
                        },
                    }}
                >
                    <Box sx={{  }}>
                        <Typography variant="h5" sx={{ fontWeight: 'bold', mr: 1 }}>
                            {activity.price}€
                        </Typography>
                        <Typography variant="h5" sx={{ fontWeight: 'normal' }}>
                            {getPriceTypeLabel(activity.priceType)}
                        </Typography>
                        <Button onClick={toggleFavorite} sx={{ minWidth: 'unset',  }}>
                            {isFavorited ? (
                                <FavoriteIcon sx={{ color: '#ff6f61' }} />
                            ) : (
                                <FavoriteBorderIcon sx={{ color: '#ff6f61' }} />
                            )}
                        </Button>
                    </Box>

                    <Divider />
                    <Box sx={{ textAlign: 'left', mt: 3, }}>
                    <Typography sx={{ mb: 1 }}>
                        ⏳ {activity.durationMinutes} min
                    </Typography>
                    <Typography sx={{ mb: 1 }}>
                        📍{activity.deliveryMethod === 'ONLINE'
                            ? 'Nuotoliu per Teams'
                            : activity.location}
                    </Typography>
                    <Typography sx={{ mb: 1 }}>
                        {getCategoryEmoji(activity.category)} {activity.category.charAt(0).toUpperCase()
                        + activity.category.slice(1).toLowerCase()}
                    </Typography>
                </Box>
                    <Divider />
                    <ButtonFunky sx={{ mt: 2 }} onClick={() => timeslotRef.current?.scrollIntoView({ behavior: 'smooth' })}>
                        Pasirinkti laiką
                    </ButtonFunky>

                    {activity.deliveryMethod === 'ONLINE' && activity.meetingUrl && (
                        <a
                            href={activity.meetingUrl}
                            target="_blank"
                            rel="noopener noreferrer"
                            style={{
                                display: 'inline-block',
                                textAlign: 'center',
                                width: '100%',
                                padding: '10px 20px',
                                backgroundColor: '#43a047',
                                color: '#fff',
                                borderRadius: '8px',
                                textDecoration: 'none',
                                fontWeight: 'bold'
                            }}
                        >
                            Prisijungti prie susitikimo
                        </a>
                    )}

                </Box>
                    {activity.deliveryMethod === 'ONSITE' && (
                        <Box sx={{width:'350px'}}>
                            <Typography variant="h5" sx={{ fontFamily: '"Comic Sans MS", cursive, sans-serif', fontWeight: 'bold', color: 'rgba(0,0,0,0.87)', mb:1 }}>
                                Vietovė
                            </Typography>
                            <Box>
                                <iframe
                                    title="Google Maps"
                                    height="300"
                                    style={{
                                        width: '100%',
                                        mb: 2,
                                        p: 2,
                                        backgroundColor: '#ffffff',
                                        border: '2px solid #422800',
                                        borderRadius: '30px',
                                        boxShadow: '4px 4px 0 0 #422800',
                                        transition: 'all 0.2s ease-in-out',
                                    }}
                                    loading="lazy"
                                    allowFullScreen
                                    src={`https://www.google.com/maps?q=${encodeURIComponent(activity.location)}&output=embed`}
                                />
                            </Box>
                        </Box>
                    )}
                </Box>
            </Box>
        </Box>
    );
};

export default ActivityCardDetails;