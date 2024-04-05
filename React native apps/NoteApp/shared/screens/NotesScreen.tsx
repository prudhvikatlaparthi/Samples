import { View, Text, Pressable, FlatList } from 'react-native';
import React, { useState } from 'react';
import { useFocusEffect, useNavigation } from '@react-navigation/native';
import { ScreenNavigationStackProp } from '../../App';
import { Note, getAllNotes } from '../repository/NotesRepository';
import styles from '../themes/AppTheme.style';
import Colors from '../themes/Colors.style';
import StaggeredList from '@mindinventory/react-native-stagger-view';

const NotesScreen: React.FC = () => {
  const navigation = useNavigation<ScreenNavigationStackProp>();
  const [notes, setNotes] = useState<Note[]>();
  useFocusEffect(() => {
    getAllNotes().then((result) => {
      setNotes(result.notes)
    })
  });
  function renderChildren(item: Note) {
    function getColor() {
      const value = Math.floor(Math.random() * 5) + 1;
      if (value === 1) {
        return Colors.color1
      } else {
        return Colors.color2
      }
    }

    return (
      <View key={item.id} style={[styles.noteItemStyle, { backgroundColor: getColor() }]}>
        <Text style={{ color: Colors.colorAccent }}>{item.text}</Text>
      </View>
    );
  }

  return (
    <View style={{ flex: 1, backgroundColor: Colors.colorOnPrimary }}>
      <StaggeredList
        data={notes}
        animationType={'FADE_IN_FAST'}
        contentContainerStyle={{}}
        showsVerticalScrollIndicator={false}
        renderItem={({ item }) => renderChildren(item)}
      />
    </View>
  );
}

export default NotesScreen;