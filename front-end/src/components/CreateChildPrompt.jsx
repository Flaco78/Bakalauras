import React from 'react';
import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Box,
    Typography
} from '@mui/material';
import ButtonFunky from "./ButtonFunky.jsx";

const CreateChildPrompt = ({
                               open,
                               onClose,
                               onCreate,
                               title = "👧👦 Sukurti vaiko profilį?",
                               message = "Atrodo, kad neturite nė vieno vaiko profilio. Ar norite jį sukurti dabar?",
                               cancelLabel = "Ne dabar",
                               confirmLabel = "Sukurti"
                           }) => (
    <Dialog
        open={open}
        onClose={onClose}
        PaperProps={{
            sx: {
                borderRadius: 4,
                p: 2
            }
        }}
    >
        {/* Use Typography instead of h4 */}
        <DialogTitle sx={{ textAlign: 'center', p: 2 }}>
            <Typography variant="h5" sx={{ fontWeight: 'bold' }}>
                {title}
            </Typography>
        </DialogTitle>

        <DialogContent>
            <Typography sx={{ textAlign: 'center', px: 2 }}>
                {message}
            </Typography>
        </DialogContent>

        <DialogActions>
            <Box sx={{ width: '100%', display: 'flex', justifyContent: 'center', gap: 2, pb: 1 }}>
                <ButtonFunky onClick={onClose} color="secondary">
                    {cancelLabel}
                </ButtonFunky>
                <ButtonFunky onClick={onCreate} color="primary" variant="contained">
                    {confirmLabel}
                </ButtonFunky>
            </Box>
        </DialogActions>
    </Dialog>
);

export default CreateChildPrompt;