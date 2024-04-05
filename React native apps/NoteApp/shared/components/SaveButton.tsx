import { Pressable, Text } from "react-native";
import { getCurrentNoteID, getCurrentNoteText, saveNote } from "../repository/NotesRepository";
import Icon from 'react-native-vector-icons/MaterialIcons';
const SaveButton: React.FC = () => {
    return (
        <Pressable onPress={async () => {
            const currentNote = await getCurrentNoteText() ?? "(Blank Note)"
            const currentID = await getCurrentNoteID()
            await saveNote(currentNote, currentID)
        }}>
           <Icon name='check' size={30}/>
        </Pressable>
    );
}

export default SaveButton;