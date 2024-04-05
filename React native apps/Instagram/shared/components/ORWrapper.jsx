import { View } from "react-native";
import AppText from "./AppText";

export default ORWrapper = () => {
    return (
        <View>
            <View style={{ flexDirection: 'row', alignItems: 'center', width: '80%', marginVertical: 10 }}>
                <View style={{ flex: 1, height: 1, backgroundColor: '#DBDBDB' }} />
                <AppText
                    style={{ textAlign: 'center', color: '#807573', paddingHorizontal: 10, fontWeight: '600' }} >OR </AppText>
                <View style={{ flex: 1, height: 1, backgroundColor: '#DBDBDB' }} />
            </View>
        </View>
    );
}