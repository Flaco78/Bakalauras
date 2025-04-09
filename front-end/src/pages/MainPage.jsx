    import React, {useEffect, useState, useCallback} from 'react';
    import axios from 'axios';
    import {useAuth} from '../context/AuthContext';
    import ActivityCard from "../components/ActivityCard.jsx";
    import {Navigate} from "react-router-dom";
    import {Box, Typography} from "@mui/material";

    const MainPage = () => {
        const [activities, setActivities] = useState([]);
        const { isAuthenticated } = useAuth();
        const token = localStorage.getItem('token') || '';
        const [categories, setCategories] = useState([]);
        const [selectedCategory, setSelectedCategory] = useState(null);
        const [viewMode, setViewMode] = useState("horizontal");
        const [recommendedActivities, setRecommendedActivities] = useState([]);

        const handleFetchActivities = useCallback((url) =>{
            axios.get(url, {
                headers: { 'Authorization': `Bearer ${token}` },
            })
                .then(response => setActivities(response.data))
                .catch(error => handleError(error));
        }, [token]);

        useEffect(() => {
            axios.get('/api/activities/recommended', {
                headers: { 'Authorization': `Bearer ${token}` }
            })
                .then(response => setRecommendedActivities(response.data))
                .catch(error => console.error("Failed to fetch recommended activities", error));
        }, [token]);

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

                // Jei paspaudė tą pačią kategoriją antrą kartą
                if (isSame) {
                    setViewMode("horizontal");
                    return null;
                }

                setViewMode(category === "All" ? "grid" : "horizontal");
                return category;
            });
        };

        return (
            <Box component="section" style={{ display: 'flex', flexDirection: 'column', alignItems: 'center',  width: '100%'}} sx={{ mt: 8 }}>
                {/* Rekomenduojamos veiklos sekcija */}
                <Box sx={{ width: '100%'}}>
                    <Typography
                        variant="h5"
                        sx={{
                            fontWeight: 600,
                            textAlign: 'center',
                            mb: 3,
                            fontSize: '28px',
                            color: '#333',
                        }}
                    >
                        Rekomenduojamos veiklos šiam vartotojui
                    </Typography>

                    {recommendedActivities.length > 0 ? (
                        <Box
                            sx={{
                                display: 'grid',
                                gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))',
                                gap: 2,
                                width: '100%',
                                paddingBottom: '30px',
                            }}
                        >
                            {recommendedActivities.map((activity) => (
                                <Box key={activity.id} sx={{ boxSizing: 'border-box' }}>
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

                {/*Kategorijos*/}
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
                    Activity categories
                </Typography>
                <Box
                    sx={{
                        top: '64px', // priderink prie navbar aukščio
                        zIndex: 10,
                        display: 'flex',
                        gap: 1, // sumažinti tarpą tarp kategorijų
                        flexWrap: 'wrap',
                        justifyContent: 'center',
                        padding: '10px 0',
                        maxWidth: '100%', // leisti sekcijai užimti visą ekraną
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
                                    border: '2px solid #422800', // Tamsesnis rudas apvadas
                                    borderRadius: '30px', // Apvalūs kampai
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
                                        backgroundColor: '#f4d6b7', // Baltas fonas, kai paspaudžiamas
                                    },
                                    '&:active': {
                                        boxShadow: '2px 2px 0 0 #422800', // Tamsesnis šešėlis aktyvumui
                                        transform: 'translate(2px, 2px)', // Šiek tiek perkeliamas aktyvumui
                                    },
                                    '@media (min-width: 768px)': {
                                        minWidth: '120px', // Minimalus plotis didesniems ekranams
                                        padding: '0 25px', // Didesnis padding didesniuose ekranuose
                                    },
                                }}
                            >
                                {category.charAt(0).toUpperCase() + category.slice(1).toLowerCase()}
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
                            gap: 5, // Užtikrinama, kad tarpai bus vienodi abiem režimams
                            justifyContent: viewMode === "horizontal" ? 'flex-start' : 'center',
                            overflowX: viewMode === "horizontal" ? 'auto' : 'visible',
                            width: '100%',
                            paddingBottom: '20px',
                        }}
                    >
                        {activities.map((activity) => (
                            <Box key={activity.id} sx={{  flex: viewMode === "horizontal" ? '0 0 auto' : '1 0 21%' }}>
                                <ActivityCard activity={activity} />
                            </Box>
                        ))}
                    </Box>
                ) : (
                    <Typography sx={{ fontSize: '20px', fontFamily: 'roboto', fontWeight: '400', textAlign: 'center', color: '#888' }}>
                        Activities cannot be found.
                    </Typography>
                )}
            </Box>
        );
    };

    export default MainPage;