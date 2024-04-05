import { View, Linking, TextInput, Image, TouchableOpacity, ScrollView, } from "react-native";
import styles from './authentication.style';
import AppButton from '../../components/AppButton';
import AppText from '../../components/AppText.jsx';
import ORWrapper from '../../components/ORWrapper';
import { useState } from "react";
import Toast from 'react-native-simple-toast';
import { StackActions } from '@react-navigation/native';



const ResetPasswordScreen = ({ navigation }) => {
    const [mobileOrEmail, setMobileOrEmail] = useState("")
    const labelName = "Email, Phone, Username"
    const popAction = StackActions.pop(1);
    return (
        <ScrollView >
            <View style={styles.parent}>
                <View style={styles.container}>
                    <Image
                        style={{ height: 100, width: 100 }}
                        source={require("../../../assets/images/lock.png")}
                    />
                    <AppText
                        style={{ color: 'black', fontSize: 16, textAlign: 'center', paddingHorizontal: 10, fontWeight: '500', marginBottom: 10 }} >Trouble logging in?</AppText>
                    <AppText
                        style={{ color: '#73737C', fontSize: 14, textAlign: 'center', paddingHorizontal: 10, fontWeight: '500', marginBottom: 10 }} >Enter your email, phone, or username and we'll send you a link to get back into your account.</AppText>
                    <TextInput
                        style={styles.inputTextStyle}
                        value={mobileOrEmail}
                        onChangeText={setMobileOrEmail}
                        placeholder={labelName}
                    />

                    <AppButton
                        buttonName={"Send login link"}
                        onPress={() => { console.log("Tap") }}
                    />

                    <TouchableOpacity onPress={() => {
                        if (mobileOrEmail.length == 0) {
                            Toast.showWithGravity(`Please provide ${labelName}`, Toast.LONG, Toast.CENTER);
                            return
                        }
                        Linking.openURL(`https://www.instagram.com/accounts/account_recovery/?username=${mobileOrEmail}`);
                    }}>
                        <AppText

                            style={{ color: '#385185', opacity: 0.9, marginBottom: 20 }}
                            fontType='bold'
                        >Can't reset your password?</AppText>
                    </TouchableOpacity>
                    <ORWrapper />

                    <TouchableOpacity onPress={() => {
                        navigation.dispatch(popAction);
                        navigation.push("RegisterScreen")
                    }}>
                        <AppText style={{ fontSize: 15 }}
                            fontType='bold'>Create new account</AppText>
                    </TouchableOpacity>

                    <View style={[{
                        marginTop: "20%",
                        alignItems: 'center',
                        justifyContent: 'center',
                        borderRadius: 1,
                        width: "100%",
                        padding: 10,
                        borderColor: '#DBDBDB',
                        borderWidth: 1
                    }]}>
                        <TouchableOpacity onPress={() => navigation.goBack()}>
                            <AppText style={{ fontSize: 15 }}
                                fontType='bold'
                            >Back to login</AppText>
                        </TouchableOpacity>
                    </View>
                </View>
            </View>
        </ScrollView>
    );
}

export default ResetPasswordScreen;