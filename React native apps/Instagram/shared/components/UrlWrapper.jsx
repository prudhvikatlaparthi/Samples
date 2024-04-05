import { View } from "react-native";
import AppText from "./AppText";


export default UrlWrapper = ({ text1, clickableText1, onPress1, clickableText2, onPress2, clickableText3, onPress3 }) => {
    return (
        <View>
            <View style={{ flexDirection: 'row', width: '90%', marginTop: 10 }}>
                <AppText style={{ color: '#807373', textAlign: 'center', fontSize: 12 }}>
                    {text1}
                    {clickableText1 != null && onPress1 != null ? <AppText style={{ color: '#00376B', fontSize: 13 }} onPress={onPress1}>
                        {clickableText1}
                    </AppText> : <></>}

                    {clickableText2 != null && onPress2 != null ? <AppText style={{ color: '#00376B', fontSize: 13 }} onPress={onPress2}>
                        {clickableText2}
                    </AppText> : <></>}

                    {clickableText3 != null && onPress3 != null ? (<AppText>
                        {" and "}
                        <AppText style={{ color: '#00376B', fontSize: 13 }} onPress={onPress3}>
                            {clickableText3}
                        </AppText></AppText>) : <></>}
                </AppText>
            </View>
        </View>
    );
}