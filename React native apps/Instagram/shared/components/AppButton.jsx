import { TouchableOpacity, Text, View } from "react-native";
import AppText from "./AppText.jsx";


export default AppButton = ({ buttonName, onPress }) => {
    return (
        <TouchableOpacity style={{
            backgroundColor: '#4CB5F9',
            borderRadius: 8,
            width: "80%",
            alignItems: 'center',
            paddingVertical: 8,
            margin: 13
        }} onPress={onPress}>
            <AppText
                style={{ color: 'white', fontWeight: 'bold' }} > {buttonName}</AppText>
        </TouchableOpacity>
    );
}