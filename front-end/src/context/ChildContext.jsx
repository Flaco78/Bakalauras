import React, { createContext, useContext, useState, useEffect } from 'react';

// Create the context for selected child ID
const ChildContext = createContext();

// Custom hook to access the Child context
export const useChild = () => {
    return useContext(ChildContext);
};

// Provider component to wrap the app with
export const ChildProvider = ({ children }) => {
    const [selectedChildId, setSelectedChildId] = useState(null);

    // Get selected child from localStorage on mount
    useEffect(() => {
        const storedChildId = localStorage.getItem('selectedChildId');
        if (storedChildId) {
            setSelectedChildId(storedChildId);
        }
    }, []);

    // Save selected child to localStorage whenever it changes
    const updateSelectedChild = (childId) => {
        setSelectedChildId(childId);
        localStorage.setItem('selectedChildId', childId);  // Save to localStorage
    };

    return (
        <ChildContext.Provider value={{ selectedChildId, updateSelectedChild }}>
            {children}
        </ChildContext.Provider>
    );
};