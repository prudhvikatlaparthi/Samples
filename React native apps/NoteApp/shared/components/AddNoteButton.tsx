import { Pressable, Text } from "react-native";
import { getCurrentNoteID, getCurrentNoteText, saveNote } from "../repository/NotesRepository";
import { useNavigation } from "@react-navigation/native";
import { ScreenNavigationStackProp } from "../../App";
import Icon from 'react-native-vector-icons/MaterialIcons';

const AddNoteButton: React.FC = () => {
    const navigation = useNavigation<ScreenNavigationStackProp>();
    return (
        <Pressable onPress={() => {
            navigation.navigate("CreateNoteScreen");
        }}>
            <Icon name='add' size={30}/>
        </Pressable>
    );
}

export default AddNoteButton;