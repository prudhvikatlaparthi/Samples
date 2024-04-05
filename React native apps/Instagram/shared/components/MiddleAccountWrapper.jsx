import { View, TouchableOpacity, Text } from "react-native";
import styles from '../screens/authentication/authentication.style';
import AppText from "./AppText.jsx";

export default MiddleAccountWrapper = ({ name1, name2, onPress }) => {
    return (
        <View>
            <View style={styles.container}>
                <View style={{ flexDirection: 'row', alignItems: 'center', justifyContent: 'center', width: '80%', margin: 20 }}>
                    <AppText style={{ marginStart: 6, color: 'black' }}
                    >{name1} </AppText>

                    <TouchableOpacity onPress={onPress}>
                        <AppText
                            style={{ marginStart: 6, color: '#0095F7', }}
                            fontType='bold'
                        > {name2} </AppText>

                    </TouchableOpacity>
                </View>
            </View>
        </View>
    );
}