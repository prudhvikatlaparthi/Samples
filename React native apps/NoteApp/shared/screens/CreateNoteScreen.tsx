import React, { useState } from 'react';
import { Text, View, TextInput } from 'react-native';
import styles from '../themes/AppTheme.style';
import { saveCurrentNoteText } from '../repository/NotesRepository';

const CreateNoteScreen: React.FC = () => {
    const [note,setNote] = useState<string>('')
    return (
        <View style={styles.container}>
            <TextInput
                style={styles.inputTextStyle}
                multiline={true}
                value={note}
                onChangeText={(value) => {
                    setNote(value)
                    saveCurrentNoteText(value)
                } }
            />
        </View>
    );
}

export default CreateNoteScreen;