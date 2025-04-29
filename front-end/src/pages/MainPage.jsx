    import React, {useEffect, useState, useCallback} from 'react';
    import axios from 'axios';
    import {useAuth} from '../context/AuthContext';
    import ActivityCard from "../components/ActivityCard.jsx";
    import {Navigate} from "react-router-dom";
    import {Alert, Box, Dialog, DialogContent, Snackbar, Typography} from "@mui/material";
    import {useChild} from "../context/ChildContext.jsx";
    import { Tabs, Tab } from '@mui/material';
    import CreateChildPrompt from "../components/CreateChildPrompt.jsx";
    import ChildProfileForm from "../forms/ChildProfileForm.jsx";

    const MainPage = () => {
        const [activities, setActivities] = useState([]);
        const { isAuthenticated, roles } = useAuth();
        const token = localStorage.getItem('token') || '';
        const [categories, setCategories] = useState([]);
        const [selectedCategory, setSelectedCategory] = useState(null);
        const [viewMode, setViewMode] = useState("horizontal");
        const [recommendedActivities, setRecommendedActivities] = useState([]);
        const [children, setChildren] = useState([]);
        const { selectedChildId } = useChild();
        const selectedChild = children.find(child => child.id === Number(selectedChildId));
        const [collabRecommendations, setCollabRecommendations] = useState([]);
        const [popularActivities, setPopularActivities] = useState([]);
        const [popularTab, setPopularTab] = useState('view');
        const [nearbyActivities, setNearbyActivities] = useState([]);
        const categoryTranslations = {
            sports: '⚽ Sportas',
            music: '🎵 Muzika',
            art: '🎨 Menas',
            science: '🔬 Mokslas',
            coding: '💻 Technologijos',
            language: '📚 Kalbos',
            nature: '🌿 Gamta',
            dance: '💃 Šokiai',
            theater: '🎭 Teatras',
            cooking: '👩‍🍳 Maisto gamyba',
            animals: '🐾 Gyvūnai',
            logic: '🧠 Loginiai žaidimai',
            movement: '🤸 Judėjimas',
            other: '✨ Kita',
            all: '🌈 Visi'
        };
        const [showPrompt, setShowPrompt] = useState(false);
        const [showForm, setShowForm] = useState(false);
        const [showSuccess, setShowSuccess] = useState(false);

        useEffect(() => {
            if (!selectedChildId || !roles.includes('USER')) return;

            axios.get(`/api/recommendations/nearby/${selectedChildId}`, {
                headers: { Authorization: `Bearer ${token}` }
            })
                .then(res => setNearbyActivities(res.data))
                .catch(err => console.error("Failed to fetch nearby activities", err));
        }, [selectedChildId, token]);

        useEffect(() => {
            axios.get(`/api/recommendations/popular?type=${popularTab}&limit=8`, {
                headers: { Authorization: `Bearer ${token}` }
            })
                .then(res => setPopularActivities(res.data))
                .catch(err => console.error("Failed to fetch popular activities", err));
        }, [token, popularTab]);

        const handleFetchActivities = useCallback((url) =>{
            axios.get(url, {
                headers: { 'Authorization': `Bearer ${token}` },
            })
                .then(response => setActivities(response.data))
                .catch(error => handleError(error));
        }, [token]);


        useEffect(() => {
            if (!roles.includes('USER')) return;

            axios.get('/api/auth/user/child-profiles', {
                headers: { Authorization: `Bearer ${token}` }
            })
                .then(res => {
                    setChildren(res.data);
                    if (res.data.length === 0) {
                        setShowPrompt(true);
                    }
                })
                .catch(err => console.error(err));
        }, [token]);

        useEffect(() => {
            if (!selectedChildId) return;

            axios.get(`/api/recommendations/collaboration-filtering/${selectedChildId}`, {
                headers: { 'Authorization': `Bearer ${token}` }
            })
                .then(response => setCollabRecommendations(response.data))
                .catch(err => console.error("Failed to fetch collaborative recommendations", err));
        }, [selectedChildId, token]);

        useEffect(() => {
            if (!selectedChildId) {
                return;
            }

            axios.get(`/api/recommendations/content-based/${selectedChildId}`, {
                headers: { 'Authorization': `Bearer ${token}` }
            })
                .then(response => setRecommendedActivities(response.data))
                .catch(error => console.error("Failed to fetch recommended activities", error));
        }, [token, selectedChildId]);



        useEffect(() => {
            axios.get('/api/activities/categories', {
                headers: { 'Authorization': `Bearer ${token}` }
            })
                .then(response => {
                    const categoryList = response.data.map(c => c.toUpperCase());
                    setCategories(["All", ...categoryList]);
                })
                .catch(err => console.error("Failed to load categories", err));
        }, [token]);


        useEffect(() => {
        if (!token) {
            console.warn("No token found. Skipping activity fetch.");
            return;
        }
            let url = "/api/activities/all";
            if (selectedCategory && selectedCategory !== "All") {
                url = `/api/activities/category/${selectedCategory}`;
            }

        handleFetchActivities(url);
        }, [selectedCategory, handleFetchActivities, token]);

        const handleError = (error) => {
            if (error.response) {
                console.error("Server error:", error.response.status, error.response.data);
            } else if (error.request) {
                console.error("Internet error:", error.request);
            } else {
                console.error("Unexpected error:", error.message);
            }
        };

        if (!isAuthenticated) {
            return <Navigate to="/login" replace />;
        }


        const handleCategoryClick = (category) => {
            setSelectedCategory(prev => {
                const isSame = prev === category;

                if (isSame) {
                    setViewMode("horizontal");
                    return null;
                }

                setViewMode(category === "All" ? "grid" : "horizontal");
                return category;
            });
        };

        return (
            <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center',  width: '100%', mt: 8}}>

                {roles.includes('PROVIDER') && (
                    <Typography variant="h6" sx={{ mt: 5 }}>
                        Prisijungėte kaip teikėjas. Pagrindinis puslapis skirtas tėvams ir vaikų veikloms.
                    </Typography>
                )}

                {roles.includes('USER') && recommendedActivities.length > 0 && (
                <Box sx={{ width: '100%'}}>
                    <Typography
                        variant="h5"
                        sx={{
                            fontWeight: 600,
                            textAlign: 'center',
                            fontSize: '28px',
                            color: '#333',
                        }}
                    >
                        {selectedChild ? (
                            <>
                                Rekomenduojamos veiklos, remiantis{' '}
                                <Box component="span" sx={{ color: '#ffb15a' }}>
                                    {selectedChild.name}
                                </Box>{' '}
                                pomėgiais
                            </>
                        ) : (
                            'Rekomenduojamos veiklos'
                        )}
                    </Typography>

                    {recommendedActivities.length > 0 ? (
                        <Box
                            sx={{
                                display: 'flex',
                                flexDirection: 'row',
                                overflowX: 'auto',
                                gap: 2,
                                width: '100%',
                                paddingBottom: '30px',
                                scrollBehavior: 'smooth',
                                WebkitOverflowScrolling: 'touch',
                            }}
                        >
                            {recommendedActivities.map((activity) => (
                                <Box
                                    key={activity.id}
                                    sx={{ minWidth: 280, flex: '0 0 auto' }}
                                >
                                    <ActivityCard activity={activity} />
                                </Box>
                            ))}
                        </Box>
                    ) : (
                        <Typography sx={{ fontSize: '20px', fontFamily: 'roboto', fontWeight: '400', textAlign: 'center', color: '#888' }}>
                            Šiuo metu nėra rekomenduojamų veiklų.
                        </Typography>
                    )}
                </Box>
                )}

                {roles.includes('USER') && collabRecommendations.length > 0 &&(
                <Box sx={{ width: '100%'}}>
                    <Typography
                        variant="h5"
                        sx={{
                            fontWeight: 600,
                            textAlign: 'center',
                            fontSize: '28px',
                            color: '#333',
                        }}
                    >
                        Panašios veiklos, kurios patiko kitiems vaikams
                    </Typography>
                {collabRecommendations.length > 0 ? (
                    <Box
                        sx={{
                            display: 'flex',
                            flexDirection: 'row',
                            overflowX: 'auto',
                            gap: 2,
                            width: '100%',
                            paddingBottom: '30px',
                            scrollBehavior: 'smooth',
                            WebkitOverflowScrolling: 'touch',
                        }}
                    >

                    {collabRecommendations.map((activity) => (
                                <Box
                                    key={activity.id}
                                    sx={{ minWidth: 280, flex: '0 0 auto' }}
                                >
                                    <ActivityCard activity={activity} />
                                </Box>
                            ))}
                        </Box>
                ) : (
                    <Typography sx={{ fontSize: '20px', fontFamily: 'roboto', fontWeight: '400', textAlign: 'center', color: '#888' }}>
                        Šiuo metu nėra rekomenduojamų veiklų.
                    </Typography>
                )}
                </Box>
                    )}

                {roles.includes('USER') && nearbyActivities.length > 0 && (
                    <Box sx={{ width: '100%', mt: 5 }}>
                        <Typography
                            variant="h5"
                            sx={{
                                fontWeight: 600,
                                textAlign: 'center',
                                fontSize: '28px',
                                color: '#333',
                                mb: 2
                            }}
                        >
                            Veiklos šalia jūsų 📍
                        </Typography>

                        <Box
                            sx={{
                                display: 'flex',
                                flexDirection: 'row',
                                overflowX: 'auto',
                                gap: 2,
                                width: '100%',
                                paddingBottom: '30px',
                                scrollBehavior: 'smooth',
                                WebkitOverflowScrolling: 'touch',
                            }}
                        >
                            {nearbyActivities.map((activity) => (
                                <Box key={activity.id} sx={{ minWidth: 280, flex: '0 0 auto' }}>
                                    <ActivityCard activity={activity} />
                                </Box>
                            ))}
                        </Box>
                    </Box>
                )}

                {roles.includes('USER') && popularActivities.length > 0 && (
                    <Box sx={{ width: '100%', mt: 5 }}>
                        <Typography
                            variant="h5"
                            sx={{
                                fontWeight: 600,
                                textAlign: 'center',
                                fontSize: '28px',
                                color: '#333',
                                mb: 2
                            }}
                        >
                            Populiarios veiklos
                        </Typography>

                        <Tabs
                            value={popularTab}
                            onChange={(e, newValue) => setPopularTab(newValue)}
                            centered
                            textColor="inherit"
                            indicatorColor="primary"
                            sx={{ mb: 3 }}
                        >
                            <Tab label="Daugiausiai peržiūrėtos 👀" value="view" />
                            <Tab label="Mėgstamiausios 🧡" value="favorite" />
                            <Tab label="Dažniausiai registruojamos 📝" value="registered" />
                        </Tabs>

                        {popularActivities.length > 0 ? (
                            <Box
                                sx={{
                                    display: 'flex',
                                    flexDirection: 'row',
                                    overflowX: 'auto',
                                    gap: 2,
                                    width: '100%',
                                    paddingBottom: '30px',
                                    scrollBehavior: 'smooth',
                                    WebkitOverflowScrolling: 'touch',
                                }}
                            >
                                {popularActivities.map((activity) => (
                                    <Box key={activity.id} sx={{ minWidth: 280, flex: '0 0 auto' }}>
                                        <ActivityCard activity={activity} />
                                    </Box>
                                ))}
                            </Box>
                        ) : (
                            <Typography sx={{ textAlign: 'center', color: '#888' }}>
                                Šiuo metu nėra veiklų šioje kategorijoje.
                            </Typography>
                        )}
                    </Box>
                )}

                <Typography
                    variant="h5"
                    sx={{
                        fontWeight: 600,
                        textAlign: 'center',
                        mb: 3,
                        fontSize: '28px',
                        color: '#333',
                        mt: 8,
                    }}
                >
                    Veiklų kategorijos
                </Typography>
                <Box
                    sx={{
                        top: '64px',
                        zIndex: 10,
                        display: 'flex',
                        gap: 1,
                        flexWrap: 'wrap',
                        justifyContent: 'center',
                        padding: '10px 0',
                        maxWidth: '100%',
                        boxSizing: 'border-box',
                        width: '100%',
                    }}
                >
                    {categories.map((category) => {
                        const isSelected = selectedCategory === category;
                        return (
                            <Box
                                key={category}
                                onClick={() => handleCategoryClick(category)
                                }
                                sx={{
                                    border: '2px solid #422800',
                                    borderRadius: '30px',
                                    boxShadow: '4px 4px 0 0 #422800',
                                    backgroundColor: isSelected ? '#ffb15a' : '#f0f0f0',
                                    color: isSelected ? '#fff' : '#000',
                                    cursor: 'pointer',
                                    display: 'inline-block',
                                    fontWeight: 600,
                                    fontSize: '18px',
                                    padding: '0 18px',
                                    lineHeight: '50px',
                                    textAlign: 'center',
                                    textDecoration: 'none',
                                    userSelect: 'none',
                                    touchAction: 'manipulation',
                                    '&:hover': {
                                        backgroundColor: '#f4d6b7',
                                    },
                                    '&:active': {
                                        boxShadow: '2px 2px 0 0 #422800',
                                        transform: 'translate(2px, 2px)',
                                    },
                                    '@media (min-width: 768px)': {
                                        minWidth: '100px',
                                        padding: '0 25px',
                                    },
                                }}
                            >
                                {categoryTranslations[category.toLowerCase()] || category}
                            </Box>
                        );
                    })}
                </Box>

                {activities.length ? (
                    <Box
                        sx={{
                            display: viewMode === "grid" ? 'grid' : 'flex',
                            gridTemplateColumns: viewMode === "grid" ? 'repeat(auto-fit, minmax(250px, 1fr))' : undefined,
                            flexWrap: viewMode === "horizontal" ? 'nowrap' : undefined,
                            justifyContent: viewMode === "horizontal" ? 'flex-start' : 'center',
                            overflowX: viewMode === "horizontal" ? 'auto' : 'visible',
                            scrollBehavior: 'smooth',
                            WebkitOverflowScrolling: 'touch',
                            width: '100%',
                            paddingBottom: '20px',
                            gap: 2,
                            px: 2,
                        }}
                    >
                        {activities.map((activity) => (
                            <Box key={activity.id}
                                 sx={{
                                     flex: viewMode === "horizontal" ? '0 0 auto' : undefined,
                                     width: viewMode === "grid" ? '100%' : undefined,
                                 }}
                            >
                                <ActivityCard activity={activity} />
                            </Box>
                        ))}
                    </Box>
                ) : (
                    <Typography sx={{ fontSize: '20px', fontFamily: 'roboto', fontWeight: '400', textAlign: 'center', color: '#888' }}>
                        Šiuo metu neįmanoma rasti veiklų.
                    </Typography>
                )}
                <CreateChildPrompt
                    open={showPrompt}
                    onClose={() => setShowPrompt(false)}
                    onCreate={() => {
                        setShowPrompt(false);
                        setShowForm(true);
                    }}
                />

                <Dialog open={showForm} onClose={() => setShowForm(false)}>
                    <DialogContent sx={{ borderRadius: '30px' }}>
                    <ChildProfileForm
                        isEdit={false}
                        onClose={() => setShowForm(false)}
                        setChildrenProfiles={setChildren}
                        onChildCreated={(newChildId) => {
                            setShowForm(false);
                            setSelectedChildId(newChildId);
                            setShowSuccess(true);
                        }}
                    />
                    </DialogContent>
                </Dialog>

                <Snackbar open={showSuccess} autoHideDuration={3000} onClose={() => setShowSuccess(false)}>
                    <Alert onClose={() => setShowSuccess(false)} severity="success" sx={{ width: '100%' }}>
                        Vaiko profilis sėkmingai sukurtas!
                    </Alert>
                </Snackbar>
            </Box>
        );
    };

    export default MainPage;